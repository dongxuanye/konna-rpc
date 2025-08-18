package com.org.konnarpc.fault.retry;

import org.junit.Test;

public class RetryStrategyTest {

    RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RetryStrategyKeys.EXPONENTIAL_BACKOFF);

    @Test
    public void testDoRetry() {

        try {
            retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
        } catch (Exception e) {
            System.out.println("重试多次失败！");
            e.printStackTrace();
        }

    }
}