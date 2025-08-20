package com.org.konnarpc.spring.boot.starter.bootstrap;

import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.registry.LocalRegistry;
import com.org.konnarpc.registry.Registry;
import com.org.konnarpc.registry.RegistryFactory;
import com.org.konnarpc.spring.boot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
/**
 * 服务提供者初始化 - 进行服务的注册
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    // 只需要启动类实现 BeanPostProcessor 接口的postProcessAfterInitialization 方法
    // 就可以在某个服务提供者Bean初始化后，执行注册服务等操作了

    /**
     * 服务提供者初始化
     *
     * @param bean bean
     * @param beanName 名字
     * @return 对象
     * @throws BeansException bean异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
        Class<?> beanClass = bean.getClass( );
        // 通过beanClass 获取 @RpcService 注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null){
            //  1.获取基本服务信息
            Class<?> interfaceClass = rpcService.interfaceClass( );
            // 某认值处理
            if (interfaceClass == void.class){
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName( );
            String serviceVersion = rpcService.serviceVersion( );
            // 2.注册服务
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig( );
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig( );
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry( ));
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo( );
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost( ));
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort( ));
            try {
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName+ "服务注册失败："+e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
