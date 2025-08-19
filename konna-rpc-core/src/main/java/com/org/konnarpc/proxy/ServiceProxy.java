package com.org.konnarpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.constant.RpcConstant;
import com.org.konnarpc.fault.retry.RetryStrategy;
import com.org.konnarpc.fault.retry.RetryStrategyFactory;
import com.org.konnarpc.fault.tolerant.TolerantStrategy;
import com.org.konnarpc.fault.tolerant.TolerantStrategyFactory;
import com.org.konnarpc.loadbalancer.LoadBalancer;
import com.org.konnarpc.loadbalancer.LoadBalancerFactory;
import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.registry.Registry;
import com.org.konnarpc.registry.RegistryFactory;
import com.org.konnarpc.serializer.JdkSerializer;
import com.org.konnarpc.serializer.Serializer;
import com.org.konnarpc.serializer.SerializerFactory;
import com.org.konnarpc.server.client.VertxClient;
import com.org.konnarpc.server.client.VertxClientFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理类(jdk动态代理)
 * Object proxy, Method method, Object[] args
 * 与静态方法不同之处在于构造rpc请求参数不同
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * 调用方法
     * @param proxy 代理对象
     * @param method 方法
     * @param args 参数
     * @return 返回调用对象
     * @throws Throwable 抛出异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass( ).getName( );
        RpcRequest rpcRequest = RpcRequest.builder( )
                .serviceName(serviceName)
                .methodName(method.getName( ))
                .parameterTypes(method.getParameterTypes( ))
                .args(args)
                .build( );
        RpcConfig rpcConfig = RpcApplication.getRpcConfig( );
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig( ).getRegistry( ));
        // 定义元信息
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo( );
        // 设置元信息的名字和版本号
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        // 看看有几个服务地址注册上去了
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey( ));
        // 选择服务
        ServiceMetaInfo selectedServiceMetaInfo = selectServiceMetaInfo(serviceName, rpcConfig, serviceMetaInfoList);
        // 通过定义客户端工厂获得处理结果
        VertxClient client = VertxClientFactory.getInstance(RpcApplication.getRpcConfig( ).getProtocol( ));
        // 引入重试策略
        RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig( ).getRetryStrategy( ));
        RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
            try {
                return client.doRequest(rpcRequest, selectedServiceMetaInfo);
            } catch (Exception e) {
                // 捕获到异常之后删除缓存，放到后面处理
//                registry.unRegister(selectedServiceMetaInfo);
                Map<String, Object> requestTolerantParamMap = new HashMap<>();
                requestTolerantParamMap.put("rpcRequest",rpcRequest);
                requestTolerantParamMap.put("selectedServiceMetaInfo",selectedServiceMetaInfo);
                requestTolerantParamMap.put("serviceMetaInfoList",serviceMetaInfoList);
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy( ));
                return tolerantStrategy.doTolerant(requestTolerantParamMap, e);
            }
        });
        return rpcResponse.getData();
    }

    /**
     * 获取服务元信息
     * @param serviceName 服务名称
     * @return 服务元信息
     */
    private ServiceMetaInfo selectServiceMetaInfo(String serviceName, RpcConfig rpcConfig,List<ServiceMetaInfo> serviceMetaInfoList) {
        // 判断一下是否为空
        if (CollUtil.isEmpty(serviceMetaInfoList)){
            throw new RuntimeException("尚未发现服务");
        }
        // 负载均衡算法
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer( ));
        // 将调用方法名(请求路径)作为负载均衡参数
        HashMap<String, Object> requestParams = new HashMap<>( );
        requestParams.put("methodName", serviceName);
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        System.out.println("选择的服务节点为："+selectedServiceMetaInfo.getServiceNodeKey( ));
        return selectedServiceMetaInfo;
    }
}
