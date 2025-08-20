package com.org.konnarpc.spring.boot.starter.annotation;

import com.org.konnarpc.protocol.ProtocolKeys;
import com.org.konnarpc.spring.boot.starter.bootstrap.RpcConsumerBootstrap;
import com.org.konnarpc.spring.boot.starter.bootstrap.RpcInitBootStrap;
import com.org.konnarpc.spring.boot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动Rpc服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootStrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 是否需要启动server
     * @return 布尔值
     */
    boolean needServer() default true;

    /**
     * 传输协议
     */
    String protocol() default ProtocolKeys.TCP;

}
