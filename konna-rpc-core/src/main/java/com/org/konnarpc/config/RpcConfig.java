package com.org.konnarpc.config;

import com.org.konnarpc.fault.retry.RetryStrategyKeys;
import com.org.konnarpc.fault.tolerant.TolerantStrategyKeys;
import com.org.konnarpc.loadbalancer.LoadBalancerKeys;
import com.org.konnarpc.protocol.ProtocolKeys;
import com.org.konnarpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "konna-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";
    
    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化方式
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 传输协议(http/tcp)
     */
    private String protocol = ProtocolKeys.TCP;

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_BACK;

}
