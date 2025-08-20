package com.org.konnarpc.spring.boot.starter.bootstrap;

import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.server.VertxServer;
import com.org.konnarpc.server.VertxServerFactory;
import com.org.konnarpc.spring.boot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

/**
 * RPC初始化
 */
@Slf4j
public class RpcInitBootStrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring 初始化时执行，初始化RPC框架
     *
     * @param importingClassMetadata 注解里面信息
     * @param registry 注册器
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
        // 获取EnableRpc注解的属性值
        boolean needServer = (boolean) Objects.requireNonNull(importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName( ))).get("needServer");

        // RPC框架初始化
        RpcApplication.init( );

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig( );

        if (needServer) {
            // 获取协议
            String protocol = (String) Objects.requireNonNull(importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName( ))).get("protocol");
            // 启动端口
            VertxServer server = VertxServerFactory.getInstance(protocol);
            server.doStart(rpcConfig.getServerPort());
            log.info("启动 server");
        }else {
            log.info("不启动 server");
        }
    }

}
