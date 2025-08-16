package com.org.konnarpc.server.tcp;

import com.org.konnarpc.server.VertxServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * 专门用于测试粘包/半包问题
 */
@Slf4j
public class VertxTcpServerDemo implements VertxServer {

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        // 去监听8888，端口，发现粘包半包问题几乎占4成
        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                String testMessage = "Hello, server!Hello, server!Hello, server!Hello, server!";
                // 判断字节数组长度，比固定长度小的为半包
                int messageLength = testMessage.getBytes().length;
                if (buffer.getBytes().length < messageLength) {
                    System.out.println("半包, length = " + buffer.getBytes().length);
                    return;
                }
                // 比固定长度大的为粘包
                if (buffer.getBytes().length > messageLength) {
                    System.out.println("粘包, length = " + buffer.getBytes().length);
                    return;
                }
                String str = new String(buffer.getBytes(0, messageLength));
                System.out.println(str);
                if (testMessage.equals(str)) {
                    System.out.println("good");
                }
            });
        });

        // 启动 TCP 服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            } else {
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServerDemo().doStart(8888);
    }
}
