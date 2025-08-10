package com.org.konnarpc.server;

/**
 * HttpServer 接口
 */
public interface HttpServer {

    /**
     * 启动 HttpServer
     *
     * @param port 端口
     */
    void doStart(int port);

}
