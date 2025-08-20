package com.org.konnarpc.spring.boot.starter.bootstrap;

import com.org.konnarpc.proxy.ServiceProxyFactory;
import com.org.konnarpc.spring.boot.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 服务消费者初始化
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * bean初始化之后执行
     *
     * @param bean bean
     * @param beanName 名字
     * @return 对象
     * @throws BeansException bean异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
        Class<?> beanClass = bean.getClass( );
        // 遍历对象的所有属性
        Field[] declaredFields = beanClass.getDeclaredFields( );
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null){
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass( );
                if (interfaceClass == void.class){
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    log.error("RpcConsumerBootstrap error: {}", e.getMessage());
                    throw new RuntimeException("为字段注入代理对象失败",e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
