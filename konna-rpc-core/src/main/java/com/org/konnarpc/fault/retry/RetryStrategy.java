package com.org.konnarpc.fault.retry;

import com.org.konnarpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {

    /**
     * 执行重试策略
     *
     * @param callable 调用
     * @return RpcResponse
     * @throws Exception 抛出异常
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;

}
