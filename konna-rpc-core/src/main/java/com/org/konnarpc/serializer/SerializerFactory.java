package com.org.konnarpc.serializer;

import com.org.konnarpc.spi.SpiLoader;

/**
 * 序列化工厂(用于获取序列化对象)
 */
public class SerializerFactory {

    /**
     * 序列化器映射 (用于实现单例模式)
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取序列化器实例
     * @param key 序列化器key
     * @return 序列化器实例
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
