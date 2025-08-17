package com.org.konnarpc.loadbalancer;

import com.org.konnarpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoadBalancerTest{
//
//    private final LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(LoadBalancerKeys.CONSISTENT_HASH_V2);
//
//    @Test
//    public void testSelect() {
//
//        // 请求参数
//        HashMap<String, Object> requestParams = new HashMap<>( );
//        requestParams.put("methodName", "orderService");
//        // 服务列表
//        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo( );
//        serviceMetaInfo1.setServiceName("orderService");
//        serviceMetaInfo1.setServiceVersion("1.0");
//        serviceMetaInfo1.setServiceHost("localhost");
//        serviceMetaInfo1.setServicePort(1234);
//        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo( );
//        serviceMetaInfo2.setServiceName("pictureService");
//        serviceMetaInfo2.setServiceVersion("1.0");
//        serviceMetaInfo2.setServiceHost("localhost");
//        serviceMetaInfo2.setServicePort(1235);
//        ServiceMetaInfo serviceMetaInfo3 = new ServiceMetaInfo( );
//        serviceMetaInfo3.setServiceName("userService");
//        serviceMetaInfo3.setServiceVersion("2.0");
//        serviceMetaInfo3.setServiceHost("localhost");
//        serviceMetaInfo3.setServicePort(1236);
//        List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(serviceMetaInfo1, serviceMetaInfo3, serviceMetaInfo2);
//        // 连续调用3次
//        ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
//        Assert.assertNotNull(serviceMetaInfo);
//        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
//        Assert.assertNotNull(serviceMetaInfo);
//        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//        System.out.println(serviceMetaInfo);
//        Assert.assertNotNull(serviceMetaInfo);
//
//    }
}