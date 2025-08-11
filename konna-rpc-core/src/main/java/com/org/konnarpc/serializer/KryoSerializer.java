package com.org.konnarpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo序列化器
 */
public class KryoSerializer implements Serializer {

    /**
     * kryo 线程不安全，使用ThreadLocal 保证每个线程只有一个kryo
     *
     * 有个有趣的问题：为什么这里的ThreadLocal不会造成内存泄露问题
     * ThreadLocal只有在某个方法内作为局部变量定义时才需要释放，方法执行结束后，
     * 因为tomcat线程池不会销毁线程，导致存储在在该线程里的threadLocalMap不会被回收，
     * 久而久之产生内存泄露问题；threadLocal作为全局变量一般加static修饰，为强引用，
     * 生命周期和容器一样，threadLocalMap的旧value会随着set新值而被垃圾回收
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 设置动态序列化和反序列化类，不提前注册所有(可能会有安全问题)
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( );
        Output output = new Output(byteArrayOutputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output, obj);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = KRYO_THREAD_LOCAL.get( ).readObject(input, clazz);
        input.close();
        return result;
    }
}
