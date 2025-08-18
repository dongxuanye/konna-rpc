package com.org.konnarpc.fault.retry;

import com.github.rholder.retry.*;
import com.org.konnarpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 指数退避 - 重试策略
 */
@Slf4j
public class ExponentialBackoffRetryStrategy implements RetryStrategy {
    /**
     * 重试方法
     *
     * @param callable 调用
     * @return RpcResponse
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        log.info("执行指数退避策略：ExponentialBackoffRetryStrategy");
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder( )
                // 捕抓异常类型
                .retryIfExceptionOfType(Exception.class)
                // 等待策略 1秒 - 10秒
                .withWaitStrategy(WaitStrategies.exponentialWait(1000, 10, TimeUnit.SECONDS))
                // 重试策略 5次
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                // 定义一个重试监听方法
                .withRetryListener(new RetryListener( ) {

                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("第{}次重试", attempt.getAttemptNumber( ));
                    }
                })
                .build( );
        return retryer.call(callable);
    }
}
