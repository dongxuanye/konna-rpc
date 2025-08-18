package com.org.konnarpc.fault.retry;

import com.github.rholder.retry.*;
import com.org.konnarpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定间隔 - 重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * 执行重试
     *
     * @param callable 调用
     * @return RpcResponse
     * @throws Exception 抛出异常
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        log.info("执行固定间隔重试策略：FixedIntervalRetryStrategy");
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder( )
                // 捕抓异常类型
                .retryIfExceptionOfType(Exception.class)
                // 等待策略 3秒
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                // 重试策略 3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
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
