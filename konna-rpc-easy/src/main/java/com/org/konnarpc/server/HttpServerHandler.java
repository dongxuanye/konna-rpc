package com.org.konnarpc.server;

import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.registry.LocalRegistry;
import com.org.konnarpc.serializer.JdkSerializer;
import com.org.konnarpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.Method;

/**
 * http请求处理
 * 为什么需要请求处理器呢？
 * 1.反序列化请求为对象，并从请求对象中获取参数。
 * 2.根据服务名称从本地注册器中获取到对应的服务实现类
 * 3.通过反射机制调用方法，得到返回结果
 * 4.对返回结果进行封装和序列化，并写入到响应中
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    /**
     * 处理请求
     * @param request 请求
     */
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final Serializer serializer = new JdkSerializer( );

        // 记录日志
        System.out.println("Received request：" + request.method( ) + " " + request.uri() );

        // 异步处理 HTTP请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes( );
            RpcRequest rpcRequest = null;
            try{
                rpcRequest = serializer.deserialize(bytes,RpcRequest.class);
            }catch (Exception e){
                e.printStackTrace( );
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse( );
            if (rpcRequest == null){
                rpcResponse.setMessage("rpcRequest is null");
                // todo 封装响应结果
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try{
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName( ));
                // 传入方法名和参数即可
                Method method = implClass.getMethod(rpcRequest.getMethodName( ), rpcRequest.getParameterTypes( ));
                Object result = method.invoke(implClass.newInstance( ), rpcRequest.getArgs( ));
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType( ));
                rpcResponse.setMessage("success");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 封装响应结果
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 封装响应结果
     * @param request 请求
     * @param rpcResponse rpc响应
     * @param serializer 序列化器
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse httpServerResponse = request.response( )
                .putHeader("Content-Type", "application/json");
        try{
            // 序列化响应结果
            byte[] bytes = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(bytes));
        }catch (Exception e){
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
