package com.org.konnarpc;

import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.constant.RpcConstant;
import com.org.konnarpc.registry.Registry;
import com.org.konnarpc.registry.RegistryFactory;
import com.org.konnarpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 框架应用
 * 相当于holder, 存放了项目全局用到的变量，双检锁单例单例模式实现
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 初始化
     * @param newRpcConfig 新配置
     */
    private static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init, config={}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig( );
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry( ));
        registry.init(registryConfig);
        log.info("registry init, config={}", registryConfig);

        // 创建并注册ShutdownHook,在JVM退出的时候执行某些操作
        Runtime.getRuntime().addShutdownHook(new Thread((registry::destroy)));
    }

    /**
     * 初始化
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取rpc配置
     * @return rpc配置
     */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if (rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
