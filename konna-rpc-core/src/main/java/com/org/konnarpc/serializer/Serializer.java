package com.org.konnarpc.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 * 为什么要实现序列化和反序列化？
 * 因为Java对象无法在网络中传输
 * 序列化：将Java对象转为可传输的字节数组
 * 反序列化：将字节数组转为Java对象
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 待序列化的对象
     * @return 字节数组
     * @throws IOException 抛出IO异常
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     *
     * @param bytes 待反序列化的字节数组
     * @param clazz 目标对象类型
     * @return 目标对象
     * @throws IOException 抛出IO异常
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

}
