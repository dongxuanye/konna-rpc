package com.org.konnarpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.protocol.*;
import com.org.konnarpc.registry.Registry;
import com.org.konnarpc.registry.RegistryFactory;
import com.org.konnarpc.server.client.VertxClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 基于vertx实现tcp的客户端
 */
@Slf4j
public class VertxTcpClient implements VertxClient {
    @Override
    public RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Throwable {
        log.info("tcp client send request to server");
        Vertx vertx = Vertx.vertx( );
        NetClient netClient = vertx.createNetClient( );
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>( );
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result ->{
            if (result.succeeded()){
                System.out.println("Connected to TCP server");
                NetSocket socket = result.result( );
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>( );
                ProtocolMessage.Header header = new ProtocolMessage.Header( );
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer((byte) Objects.requireNonNull(ProtocolMessageSerializerEnum
                        .getEnumByValue(RpcApplication.getRpcConfig( ).getSerializer( ))).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);
                try {
                    Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                    socket.write(encodeBuffer);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误",e);
                }
                socket.handler(new TcpBufferHandlerWrapper(buffer -> {
                    try {
                        header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
                        protocolMessage.setHeader(header);
                        ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseFuture.complete(rpcResponseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }else {
                System.err.println("Failed to connect to TCP server" );
                // 假如连接失败，则抛出运行时异常
                responseFuture.completeExceptionally(new RuntimeException("Failed to connect to TCP server"));
            }
        });
        // 同步：等待接收到响应结果再执行
        RpcResponse rpcResponse = responseFuture.get( );
        netClient.close();
        return rpcResponse;
    }


}
