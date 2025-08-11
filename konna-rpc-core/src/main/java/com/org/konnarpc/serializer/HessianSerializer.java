package com.org.konnarpc.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * Hessian 序列化器
 */
public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream( );
        Hessian2Output ho = new Hessian2Output();
        ho.writeObject(obj);
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(bis);
        return (T) hi.readObject(clazz);
    }
}
