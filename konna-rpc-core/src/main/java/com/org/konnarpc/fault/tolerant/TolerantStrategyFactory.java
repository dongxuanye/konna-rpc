package com.org.konnarpc.fault.tolerant;

import com.org.konnarpc.spi.SpiLoader;

public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_STRATEGY = new FailSafeTolerantStrategy();

    /**
     * 获取实例
     * @param key 键
     * @return 容错策略
     */
    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }


}
