package com.org.konnarpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置文件工具类
 */
public class ConfigUtils {

    /**
     * 加载配置文件
     *
     * @param tClass 类型
     * @param prefix 前置
     * @param <T> 泛型
     * @return 配置类
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix){
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置文件
     *
     * @param tClass 类型
     * @param prefix 前置
     * @param env 环境
     * @param <T> 泛型
     * @return 配置类
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String env){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)){
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString( ));
        return props.toBean(tClass, prefix);
    }

}
