package com.org.konnarpc.fault.tolerant;

import com.org.konnarpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     * 容错处理
     *
     * @param context 上下文
     * @param e       异常
     * @return 容错结果
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);

}
