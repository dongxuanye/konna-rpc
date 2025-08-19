package com.org.konnarpc.fault.tolerant;

import com.org.konnarpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败 - 容错策略（立刻通知外层调用方）
 */
public class FailFastTolerantStrategy implements TolerantStrategy {


    /**
     * 快速失败
     * 直接把报错抛出去
     *
     * @param context 上下文
     * @param e       错误信息
     * @return RpcResponse
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("快速失败容错处理 - 服务报错",e);
    }
}
