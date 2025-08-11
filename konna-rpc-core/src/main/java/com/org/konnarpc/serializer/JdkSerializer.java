package com.org.konnarpc.serializer;

import java.io.*;

/**
 * jdk序列化器
 */
public class JdkSerializer implements Serializer{

    /**
     * jdk序列化
     *
     * @param obj 待序列化的对象
     * @return 字节数组
     * @throws IOException 抛出IO异常
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    /**
     * jdk反序列化
     *
     * @param bytes 待反序列化的字节数组
     * @param clazz 目标对象类型
     * @return 目标对象
     * @throws IOException 抛出IO异常
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            objectInputStream.close();
        }
    }
}
