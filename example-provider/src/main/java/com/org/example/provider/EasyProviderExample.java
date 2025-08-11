package com.org.example.provider;

import com.org.example.common.service.UserService;
import com.org.konnarpc.registry.LocalRegistry;
import com.org.konnarpc.server.HttpServer;
import com.org.konnarpc.server.VertxHttpServer;
import com.org.konnarpc.RpcApplication;

public class EasyProviderExample {

    public static void main(String[] args) {
        // RPC框架初始化
        RpcApplication.init();

        // 启动时把服务注册到本地注册中心
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 提供服务
        HttpServer server = new VertxHttpServer( );
        server.doStart(8080);
    }
}
