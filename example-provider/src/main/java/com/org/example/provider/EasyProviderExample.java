package com.org.example.provider;

import com.org.example.common.service.UserService;
import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.registry.LocalRegistry;
import com.org.konnarpc.registry.Registry;
import com.org.konnarpc.registry.RegistryFactory;
import com.org.konnarpc.server.VertxServer;
import com.org.konnarpc.server.VertxServerFactory;
import com.org.konnarpc.server.http.VertxHttpServer;
import com.org.konnarpc.RpcApplication;

public class EasyProviderExample {

    public static void main(String[] args) {
        // RPC框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName( );
        // 一个管理接口到类的映射，一个管理服务到实例地址的映射
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 启动时把服务注册到本地注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig( );
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig( );
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry( ));
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo( );
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost( ));
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort( ));
        try {
            registry.register(serviceMetaInfo);
        }catch (Exception e){
            throw new RuntimeException("服务注册失败："+e);
        }

       // 通过工厂模式可以获得服务
        VertxServer server = VertxServerFactory.getInstance(rpcConfig.getProtocol( ));
        server.doStart(rpcConfig.getServerPort());
    }
}
