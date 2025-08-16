package com.org.konnarpc.server;

/**
 * VertxServer 接口
 */
public interface VertxServer {

    /**
     * 启动 VertxServer
     *
     * @param port 端口
     */
    void doStart(int port);

}
