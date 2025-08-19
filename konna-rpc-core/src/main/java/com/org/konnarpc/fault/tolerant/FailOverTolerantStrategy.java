package com.org.konnarpc.fault.tolerant;

import cn.hutool.core.collection.CollUtil;
import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.fault.retry.RetryStrategy;
import com.org.konnarpc.fault.retry.RetryStrategyFactory;
import com.org.konnarpc.loadbalancer.LoadBalancer;
import com.org.konnarpc.loadbalancer.LoadBalancerFactory;
import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.server.client.VertxClient;
import com.org.konnarpc.server.client.VertxClientFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 转移到其他服务节点 - 容错策略
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    /**
     * 转移到其他服务节点
     * 容错方法的上下文传递所有服务节点和本次调用的服务节点，选择一个其它节点再次发起调用
     *
     * @param context 上下文
     * @param e       异常
     * @return 响应结果
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("转移到其他服务节点 - 容错策略");
        //获取其它节点并调用
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get("serviceMetaInfoList");
        ServiceMetaInfo selectedServiceMetaInfo = (ServiceMetaInfo) context.get("selectedServiceMetaInfo");

        // 将不可变列表转换为可变列表
        List<ServiceMetaInfo> mutableServiceMetaInfoList = new ArrayList<>(serviceMetaInfoList);

        // 移除失败的节点
        mutableServiceMetaInfoList.remove(selectedServiceMetaInfo);

        // 从配置文件种获取负载均衡
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        Map<String, Object> requestParamMap = new HashMap<>();
        requestParamMap.put("methodName", rpcRequest.getMethodName());

        // 通过定义客户端工厂获得处理结果
        VertxClient client = VertxClientFactory.getInstance(RpcApplication.getRpcConfig( ).getProtocol( ));
        RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig( ).getRetryStrategy( ));
        RpcResponse rpcResponse = null;
        while (!mutableServiceMetaInfoList.isEmpty( ) || rpcResponse != null){
            ServiceMetaInfo currentServiceMetaInfo = loadBalancer.select(requestParamMap, mutableServiceMetaInfoList);
            System.out.println("通过负载均衡策略，获取节点：" + currentServiceMetaInfo);
            try {
                rpcResponse = retryStrategy.doRetry(()-> client.doRequest(rpcRequest, currentServiceMetaInfo));
                log.info("通过转移其他服务节点：{}", currentServiceMetaInfo);
                return rpcResponse;
            } catch (Exception ex) {
                System.err.println("服了，这个节点用不了："+ currentServiceMetaInfo.getServiceNodeKey());
                //移除失败节点
                mutableServiceMetaInfoList.remove(currentServiceMetaInfo);
            }

        }
        throw new RuntimeException("拼尽全力也无法成功转移！！！");
    }
}
