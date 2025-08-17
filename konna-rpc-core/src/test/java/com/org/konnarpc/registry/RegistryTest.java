package com.org.konnarpc.registry;

import com.org.konnarpc.config.RegistryConfig;
import com.org.konnarpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegistryTest {

//    final Registry registry = new EtcdRegistry();
//
//    @Before
//    public void init() {
//        RegistryConfig registryConfig = new RegistryConfig();
//        registryConfig.setAddress("http://localhost:2379");
//        registry.init(registryConfig);
//    }
//
//    @Test
//    public void register() throws Exception {
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName("myService");
//        serviceMetaInfo.setServiceVersion("1.0");
//        serviceMetaInfo.setServiceHost("localhost");
//        serviceMetaInfo.setServicePort(1234);
//        registry.register(serviceMetaInfo);
//        serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName("myService");
//        serviceMetaInfo.setServiceVersion("1.0");
//        serviceMetaInfo.setServiceHost("localhost");
//        serviceMetaInfo.setServicePort(1235);
//        registry.register(serviceMetaInfo);
//        serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName("myService");
//        serviceMetaInfo.setServiceVersion("2.0");
//        serviceMetaInfo.setServiceHost("localhost");
//        serviceMetaInfo.setServicePort(1234);
//        registry.register(serviceMetaInfo);
////        this.serviceDiscovery();
////        this.unRegister();
//    }
//
//    @Test
//    public void unRegister() throws ExecutionException, InterruptedException {
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName("myService");
//        serviceMetaInfo.setServiceVersion("1.0");
//        serviceMetaInfo.setServiceHost("localhost");
//        serviceMetaInfo.setServicePort(1234);
//        registry.unRegister(serviceMetaInfo);
//    }
//
//    @Test
//    public void serviceDiscovery() {
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName("myService");
//        serviceMetaInfo.setServiceVersion("1.0");
//        String serviceKey = serviceMetaInfo.getServiceKey();
//        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
//        Assert.assertNotNull(serviceMetaInfoList);
//    }
//
//    @Test
//    public void heartBeat() throws Exception {
//        // init方法重已经执行心跳检测了
//        register();
//        // 阻塞当前线程1分钟
//        Thread.sleep(60 * 1000L);
//    }
}