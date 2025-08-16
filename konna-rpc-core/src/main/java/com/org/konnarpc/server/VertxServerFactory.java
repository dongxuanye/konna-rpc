package com.org.konnarpc.server;

import com.org.konnarpc.spi.SpiLoader;

/**
 * 传输协议工厂
 */
public class VertxServerFactory {

    static {
        SpiLoader.load(VertxServer.class);
    }

    public static VertxServer getInstance(String key) {
        return SpiLoader.getInstance(VertxServer.class, key);
    }

}
