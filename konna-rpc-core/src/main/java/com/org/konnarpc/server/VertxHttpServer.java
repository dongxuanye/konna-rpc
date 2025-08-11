package com.org.konnarpc.server;

import io.vertx.core.Vertx;

/**
 * 基于 vert.x实现方法的http服务
 */
public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        // 创建vert.x实例
        Vertx vertx = Vertx.vertx( );

        // 创建http服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer( );

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动HTTP 服务器并监听指定端口
        server.listen(port, result -> {
            // 成功后打印结果
            if (result.succeeded()) {
                System.out.println("HTTP server started on port " + port);
            } else {
                System.out.println("Failed to start HTTP server: " + result.cause());
            }
        });
    }
}



































