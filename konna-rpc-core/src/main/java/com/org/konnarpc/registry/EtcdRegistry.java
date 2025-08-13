package com.org.konnarpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * etcd 注册中心
 */
@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
//    @Deprecated
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 注册中心服务缓存（支持多个服务键）
     */
    private final RegistryServiceMultiCache registryServiceMultiCache = new RegistryServiceMultiCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();


    /**
     * 初始化
     *
     * 初始化一个kv客户端
     *
     * @param registryConfig 注册中心配置
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient( );
        heartBeat();
    }

    /**
     * 注册服务
     *
     * 创建租约客户端，并设置服务节点信息
     *
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建Lease和KV客户端
        Lease leaseClient = client.getLeaseClient( );

        // 创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get( ).getID( );

        // 设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey( );
        // KEY为层级结构的服务节点，VALUE则为服务节点信息
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder( ).withLeaseId(leaseId).build( );
        kvClient.put(key, value, putOption).get( );

        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registryKey);
    }

    /**
     * 注销服务
     *
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        String registry = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey( );
        ByteSequence key = ByteSequence.from(registry, StandardCharsets.UTF_8);
        // get原因是.get() 方法是 CompletableFuture 提供的一个阻塞调用，它会等待异步操作完成，并返回操作的结果。
        // 如果不调用 .get()，程序可能在删除操作实际完成之前就继续执行后面的代码，这可能导致你认为删除操作没有成功，而实际上它只是还没有完成。
        kvClient.delete(key).get();
        // 也要从本地缓存移除
        localRegisterNodeKeySet.remove(registry);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存重获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        // 优化后的代码，支持多个服务同时缓存
//        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceMultiCache.readCache(serviceKey);
        if (CollUtil.isNotEmpty(cachedServiceMetaInfoList)){
            log.info("从缓存中获取服务列表");
            return cachedServiceMetaInfoList;
        }

        // 前缀搜索，结尾记得加'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/" ;

        try{
            // 支持前缀匹配
            GetOption getOption = GetOption.builder( )
                    .isPrefix(true)
                    .build( );
            // 获得kv存储的值
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get( ).getKvs( );
            // 把元信息列表筛选出来，转换成ServiceMetaInfo对象
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream( )
                    .map(kv -> {
                        // 监听这个key值
                        // TODO 在多个key情况 这样是错误的，因为watch的key和本地缓存的key不一样
                        String key = kv.getKey( ).toString(StandardCharsets.UTF_8);
                        watch(key);
                        String value = kv.getValue( ).toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).toList( );
            //存储到缓存中
//            registryServiceMultiCache.writeCache(serviceKey, serviceMetaInfoList);
            //监听
//            watch(serviceKey);

            // 写入缓存重
            registryServiceCache.writeCache(serviceMetaInfoList);
//            registryServiceMultiCache.writeCache(serviceKey, serviceMetaInfoList);
            log.info("从etcd中获取服务列表");
            return serviceMetaInfoList;
        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("调用了destroy方法，当前节点下线");
        // 下线节点
        // 遍历本节点所有的KEY
        for (String key : localRegisterNodeKeySet) {
            try{
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            }catch (Exception e){
                throw new RuntimeException(key + "节点下线失败：",e);
            }
        }
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    /**
     * 心跳检测机制
     */
    @Override
    public void heartBeat() {
        // 10秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task( ) {
            @Override
            public void execute() {
                // 遍历本节点所有的key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get( )
                                .getKvs( );
                        // 该节点已过期(需要重启节点才能重新注册)
                        if (CollUtil.isEmpty(keyValues)){
                            continue;
                        }
                        // 节点未过期，重新注册 (相当于续签)
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue( ).toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        log.info("节点 {} 续签成功", key);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    /**
     * 监听服务
     *
     * @param serviceNodeKey 服务节点key
     */
    @Override
    public void watch(String serviceNodeKey) {
        // new 一个etcd的监听客户端
        Watch watchClient = client.getWatchClient();
        // 之前没有被监听就开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8),watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents( )) {
                    // key触发删除时
                    switch (event.getEventType()){
                        case DELETE:
                            // 清除缓存key
                            registryServiceCache.clearCache();
                            // 优化后的代码，支持多个服务同时缓存
//                            registryServiceMultiCache.clearCache(serviceKey);
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }

    }

}
