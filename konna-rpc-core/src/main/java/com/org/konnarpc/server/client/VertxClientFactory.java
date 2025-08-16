package com.org.konnarpc.server.client;

import com.org.konnarpc.spi.SpiLoader;

/**
 * vert.x客户端工厂
 *
 */
public class VertxClientFactory {
    static {
        SpiLoader.load(VertxClient.class);
    }

    public static VertxClient getInstance(String key) {
        return SpiLoader.getInstance(VertxClient.class, key);
    }
}
