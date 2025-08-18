package com.org.konnarpc.fault.retry;

import com.org.konnarpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 不重试 - 重试策略
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy {

    /**
     * 重试
     *
     * @param callable 调用
     * @return RpcResponse
     * @throws Exception 抛出异常
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.info("执行不重试策略：NoRetryStrategy");
        return callable.call();
    }

}
