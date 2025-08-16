package com.org.konnarpc.protocol;

import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.serializer.Serializer;
import com.org.konnarpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息解码器
 */
public class ProtocolMessageDecoder {

    /*
    // 第一次
    Hello, server!Hello, server!
    // 第二次
    Hello, server!Hello, server!Hello, server!
    每次接收到的数据更加少了，称之为半包问题
     */

    //Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!
    // 每次接收到的数据更加少了，称之为粘包问题

    // 如何解决半包问题 http ser ver

    // 如何解决粘包问题 http server htt

    /**
     * 解码
     *
     * @param buffer vert.x支持的缓存
     * @return 协议消息
     * @throws IOException io异常
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 分别从指定位置读出Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header( );
        byte magic = buffer.getByte(0);
        // 检验魔数
        if (magic != ProtocolConstant.PROTOCOL_MAGIC){
            throw new RuntimeException( "消息magic非法" );
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        // 解决粘包问题，只读指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength( ));
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer( ));
        if (serializerEnum == null){
            throw new RuntimeException( "序列化协议消息不存在" );
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue( ));
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType( ));
        if (messageTypeEnum == null){
            throw new RuntimeException( "消息类型不存在" );
        }
        switch (messageTypeEnum){
            case REQUEST:
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                System.out.println("请求解码");
                return new ProtocolMessage<>(header, rpcRequest);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                System.out.println("响应解码");
                return new ProtocolMessage<>(header, rpcResponse);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException( "暂不支持该消息类型" );
        }
    }

}
