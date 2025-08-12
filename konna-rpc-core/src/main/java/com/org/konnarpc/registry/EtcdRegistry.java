package com.org.konnarpc.registry;

import cn.hutool.json.JSONUtil;
import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * etcd 注册中心
 */
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private static final String ETCD_ROOT_PATH = "/rpc/";

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
        // todo 心跳机制
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
    }

    /**
     * 注销服务
     *
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        ByteSequence key = ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey( ), StandardCharsets.UTF_8);
        // get原因是.get() 方法是 CompletableFuture 提供的一个阻塞调用，它会等待异步操作完成，并返回操作的结果。
        // 如果不调用 .get()，程序可能在删除操作实际完成之前就继续执行后面的代码，这可能导致你认为删除操作没有成功，而实际上它只是还没有完成。
        kvClient.delete(key).get();
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 前缀搜索，结尾记得加'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/" ;
        //

        try{
            // 支持前缀匹配
            GetOption getOption = GetOption.builder( )
                    .isPrefix(true)
                    .build( );
            // 获得kv存储的值
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get( ).getKvs( );
            // 把元信息列表筛选出来，转换成ServiceMetaInfo对象
            return keyValues.stream()
                    .map(kv ->{
                        String value = kv.getValue( ).toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).toList();
        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

}
