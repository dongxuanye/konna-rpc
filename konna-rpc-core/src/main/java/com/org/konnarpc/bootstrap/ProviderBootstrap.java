package com.org.konnarpc.bootstrap;

import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.model.ServiceRegisterInfo;
import com.org.konnarpc.registry.LocalRegistry;
import com.org.konnarpc.registry.Registry;
import com.org.konnarpc.registry.RegistryFactory;
import com.org.konnarpc.server.VertxServer;
import com.org.konnarpc.server.VertxServerFactory;

import java.util.List;

/**
 * 服务提供者启动类(初始化)
 */
public class ProviderBootstrap {

    /**
     * 初始化
     * @param serviceRegisterInfoList 需要注册的服务列表
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        // RPC框架初始化（配置和注册中心）
        RpcApplication.init();
        // 获取全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName( );
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass( ));

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo( );
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败：" + e);
            }
        }
        // 获取服务端工厂并启动
        VertxServer server = VertxServerFactory.getInstance(rpcConfig.getProtocol( ));
        server.doStart(rpcConfig.getServerPort());
    }

}





























