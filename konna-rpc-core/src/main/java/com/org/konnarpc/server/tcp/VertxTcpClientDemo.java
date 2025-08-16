package com.org.konnarpc.server.tcp;

import io.vertx.core.Vertx;

/**
 * 专门用于测试粘包/半包问题
 */
public class VertxTcpClientDemo {

    public void start() {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 通过监听端口 8888，连接成功之后发送1000次 hello包
        vertx.createNetClient().connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Connected to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();
                for (int i = 0; i < 1000; i++) {
                    // 发送数据
                    socket.write("Hello, server!Hello, server!Hello, server!Hello, server!");
                }
                // 接收响应
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClientDemo().start();
    }
}
