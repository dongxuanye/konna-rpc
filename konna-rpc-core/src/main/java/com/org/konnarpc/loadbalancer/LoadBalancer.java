package com.org.konnarpc.loadbalancer;

import com.org.konnarpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器(消费端使用)
 * 负载均衡的作用是从一组可用的服务提供者中选择一个进行调用
 *
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     *
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 服务列表
     * @return 服务实例
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);

}
