package com.org.konnarpc.server.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.serializer.Serializer;
import com.org.konnarpc.serializer.SerializerFactory;
import com.org.konnarpc.server.client.VertxClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于vertx实现http的客户端
 */
@Slf4j
public class VertxHttpClient implements VertxClient {
    @Override
    public RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception {
        log.info("http client send request to server");
        // 获得请求之后，先从配置中获取序列化方式
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig( ).getSerializer( ));
        // 对请求结果进行序列化
        byte[] bodyBytes = serializer.serialize(rpcRequest);
        // 调用hutool工具类发送请求
        HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo.getServiceAddress( )).body(bodyBytes).execute( );
        // 等到响应之后将字节数组反序列化
        byte[] result = httpResponse.bodyBytes( );
        httpResponse.close();
        return serializer.deserialize(result, RpcResponse.class);
    }
}
