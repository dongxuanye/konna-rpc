package com.org.konnarpc.loadbalancer;

import com.org.konnarpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 * 这里如果想让独立维护单个服务的话，
 * 可以建一个<String,AtomicInteger>(服务名。轮询计数)
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    /**
     * 当前轮询的下标
     */
    private final AtomicInteger currentIndex = new AtomicInteger( 0 );

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()){
            return null;
        }
        // 只有一个服务，无需轮询
        int size = serviceMetaInfoList.size( );
        if (size == 1){
            return serviceMetaInfoList.get(0);
        }
        // 取模算法轮询
        int index = currentIndex.getAndIncrement( ) % size;
        return serviceMetaInfoList.get(index);
    }

}
