package com.org.konnarpc.fault.tolerant;

/**
 * 容错策略键名常量
 */
public interface TolerantStrategyKeys {

    /**
     * 服务降级(拼好服)
     */
    String FAIL_BACK = "failBack";

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";

    /**
     * 服务转移
     */
    String FAIL_OVER = "failOver";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";

}
