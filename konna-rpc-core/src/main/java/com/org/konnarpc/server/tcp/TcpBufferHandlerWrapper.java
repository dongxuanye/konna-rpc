package com.org.konnarpc.server.tcp;

import com.org.konnarpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * 使用装饰者模式 (使用recordParser 对原有的buffer处理能力进行增强 ) 解决TCP粘包问题
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    // 可以通过Vert.x框架中RecordParser(记录解析器)来解决粘包/半包问题
    /**
     * 在实际运用中，消息的长度是不固定的，所以要通过调整RecordParser的
     * 固定长度(边长)来解决。
     * 将完整的消息拆分为2次
     * 1.先完整读取请求头信息，由于请求头信息长度是固定的，可以使用RecordParser
     * 保证每次都完整读取。
     * 2.再根据请求头长度信息更改RecordParser的固定长度，保证完整获取到请求体。
     */
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler){
        // 构造parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>( ) {

            // 初始化
            int size = -1;

            // 一次完整的读取(头 + 体)
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if (-1 == size){
                    // 读取消息体长度
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // 写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                }else {
                    // 写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    // 已拼接为完整 Buffer, 执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}
