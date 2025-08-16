package com.org.konnarpc.protocol;

import com.org.konnarpc.serializer.Serializer;
import com.org.konnarpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息编码器
 */
public class ProtocolMessageEncoder {

    /**
     * 对协议消息进行编码
     * @param protocolMessage 协议消息
     * @return Buffer
     * @throws IOException io异常
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        // 判断是否为空值
        if (protocolMessage == null || protocolMessage.getHeader() == null){
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader( );
        // 一次向缓冲区中写入字节
        Buffer buffer = Buffer.buffer( );
        // 写入魔数、版本号、序列化方式、消息类型、状态码、请求ID
        buffer.appendByte(header.getMagic( ));
        buffer.appendByte(header.getVersion( ));
        buffer.appendByte(header.getSerializer( ));
        buffer.appendByte(header.getType( ));
        buffer.appendByte(header.getStatus( ));
        buffer.appendLong(header.getRequestId( ));
        if (header.getType() == ProtocolMessageTypeEnum.REQUEST.getKey( )){
            System.out.println("请求编码");
        }else if (header.getType() == ProtocolMessageTypeEnum.RESPONSE.getKey( )){
            System.out.println("响应编码");
        }
        // 通过刚刚写好的序列化枚举获取序列化方式
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer( ));
        if (serializerEnum == null){
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody( ));
        // 写入body长度和数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }

}
