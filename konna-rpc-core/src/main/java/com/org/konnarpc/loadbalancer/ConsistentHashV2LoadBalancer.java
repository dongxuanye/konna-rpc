package com.org.konnarpc.loadbalancer;

import com.org.konnarpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConsistentHashV2LoadBalancer implements LoadBalancer {

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    /**
     * 一致性Hash环，存放虚拟节点 → 使用线程安全的 ConcurrentSkipListMap
     * 支持高并发读写，且天然有序
     */
    private final ConcurrentSkipListMap<Integer, ServiceMetaInfo> virtualNodes = new ConcurrentSkipListMap<>();

    /**
     * 记录当前已加入的服务地址（用于增量更新判断）
     */
    private final Set<String> currentNodeAddresses = ConcurrentHashMap.newKeySet();

    /**
     * 锁：用于控制哈希环重建（避免多个线程同时重建）
     */
    private final Object updateLock = new Object();

    /**
     * 哈希函数接口（使用稳定哈希算法）
     */
    private final HashFunction hashFunction = new MurmurHashFunction();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
            return null;
        }

        //  1. 更新哈希环（仅当服务列表变化时重建）
        updateVirtualNodesIfNeeded(serviceMetaInfoList);

        //  2. 执行查询（纯读操作，高性能）
        String key = extractRoutingKey(requestParams);
        int hash = hashFunction.hash(key);

        // 找到第一个 >= hash 的节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry != null) {
            return entry.getValue();
        }

        // 环形回绕：取第一个节点
        Map.Entry<Integer, ServiceMetaInfo> firstEntry = virtualNodes.firstEntry();
        return firstEntry != null ? firstEntry.getValue() : null;
    }

    /**
     * 只有当服务实例列表变化时，才重建哈希环
     */
    private void updateVirtualNodesIfNeeded(List<ServiceMetaInfo> serviceMetaInfoList) {
        // 构建当前健康节点地址集合
        Set<String> newAddresses = serviceMetaInfoList.stream()
                .map(ServiceMetaInfo::getServiceAddress)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        // 无变化则跳过
        if (newAddresses.equals(currentNodeAddresses)) {
            return;
        }

        // 加锁重建，避免并发冲突
        synchronized (updateLock) {
            // 再次检查（双重检查）
            if (newAddresses.equals(currentNodeAddresses)) {
                return;
            }

            // 重建哈希环
            ConcurrentSkipListMap<Integer, ServiceMetaInfo> newVirtualNodes = new ConcurrentSkipListMap<>();
            for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
                for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                    // 使用服务名+地址+索引，防止冲突
                    String virtualNodeKey = String.format("%s#%s#%d",
                            serviceMetaInfo.getServiceName(),
                            serviceMetaInfo.getServiceAddress(),
                            i);
                    int hash = hashFunction.hash(virtualNodeKey);
                    newVirtualNodes.put(hash, serviceMetaInfo);
                }
            }

            // 原子性替换
            virtualNodes.clear();
            virtualNodes.putAll(newVirtualNodes);
            currentNodeAddresses.clear();
            currentNodeAddresses.addAll(newAddresses);
        }
    }

    /**
     * 提取路由键：优先使用 clientIP，其次 userId，最后 fallback
     */
    private String extractRoutingKey(Map<String, Object> requestParams) {
        Object serviceName = requestParams.get("methodName");
        if (serviceName != null && !String.valueOf(serviceName).trim().isEmpty()) {
            return String.valueOf(serviceName).trim();
        }
        return "6666";
    }

    /**
     * 稳定哈希函数接口
     */
    private interface HashFunction {
        int hash(String key);
    }

    public static class MurmurHashFunction implements HashFunction {
        private static final int SEED = 13331; // 可自定义种子

        @Override
        public int hash(String key) {
            byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
            int len = bytes.length;
            int h1 = SEED;

            for (int i = 0; i < len; i += 4) {
                int k1 = 0;
                switch (len - i) {
                    case 1:
                        k1 = bytes[i] & 0xff;
                        break;
                    case 2:
                        k1 = (bytes[i] & 0xff) | ((bytes[i + 1] & 0xff) << 8);
                        break;
                    case 3:
                        k1 = (bytes[i] & 0xff) | ((bytes[i + 1] & 0xff) << 8) | ((bytes[i + 2] & 0xff) << 16);
                        break;
                    default:
                        k1 = (bytes[i] & 0xff) |
                                ((bytes[i + 1] & 0xff) << 8) |
                                ((bytes[i + 2] & 0xff) << 16) |
                                ((bytes[i + 3] & 0xff) << 24);
                        break;
                }

                k1 *= 0xcc9e2d51;
                k1 = Integer.rotateLeft(k1, 15);
                k1 *= 0x1b873593;
                h1 ^= k1;
                h1 = Integer.rotateLeft(h1, 13);
                h1 = h1 * 5 + 0xe6546b64;
            }

            h1 ^= len;
            h1 ^= (h1 >>> 16);
            h1 *= 0x85ebca6b;
            h1 ^= (h1 >>> 13);
            h1 *= 0xc2b2ae35;
            h1 ^= (h1 >>> 16);

            return h1;
        }
    }
}