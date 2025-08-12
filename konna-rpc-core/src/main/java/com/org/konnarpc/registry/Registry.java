package com.org.konnarpc.registry;

import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 注册中心接口
 *
 * 遵循可扩展设计，先写一个注册中心接口，后续可以实现多种不同的注册中心，并且和序列化器
 * 一样，可以使用SPI机制动态加载
 */
public interface Registry {

    /**
     * 初始化
     *
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务 (服务端)
     *
     * @param serviceMetaInfo 服务元信息
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务 (服务端)
     *
     * @param serviceMetaInfo 服务元信息
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;

    /**
     * 服务发现 (获取某个服务的所有节点，消费端)
     *
     * @param serviceKey 服务key
     * @return 服务元信息列表
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();
}
