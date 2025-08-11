package com.org.konnarpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.serializer.JdkSerializer;
import com.org.konnarpc.serializer.Serializer;
import com.org.konnarpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理类(jdk动态代理)
 * Object proxy, Method method, Object[] args
 * 与静态方法不同之处在于构造rpc请求参数不同
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * 调用方法
     * @param proxy 代理对象
     * @param method 方法
     * @param args 参数
     * @return 返回调用对象
     * @throws Throwable 抛出异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder( )
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build( );
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            // todo 这里地址被硬编码了需要使用注册中心和服务发现机制解决
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()){
                byte[] result = httpResponse.bodyBytes( );
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData( );
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
