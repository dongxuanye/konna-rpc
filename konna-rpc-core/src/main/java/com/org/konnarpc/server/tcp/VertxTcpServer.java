package com.org.konnarpc.server.tcp;

import com.org.konnarpc.server.VertxServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * 基于 vert.x实现方法的tcp服务
 */
public class VertxTcpServer implements VertxServer {

    private byte[] handleRequest(byte[] requestData){
        // 在这里编写处理请求的逻辑，根据requestData构造响应数据并返回
        // 这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
        return "Hello, client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        // 创建vert.x实例
        Vertx vertx = Vertx.vertx( );

        // 创建TCP服务器
        NetServer server = vertx.createNetServer( );

        // 监听端口并处理请求
        server.connectHandler(new TcpServerHandler());

        // 启动TCP服务器并监听指定端口
        server.listen(port,result->{
           if (result.succeeded()){
               System.out.println("TCP server started on port " + port);
           }else {
               System.out.println("Failed to start TCP server: " + result.cause());
           }
        });
    }
}
