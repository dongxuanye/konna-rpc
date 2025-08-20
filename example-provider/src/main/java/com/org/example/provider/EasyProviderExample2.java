package com.org.example.provider;

import com.org.example.common.service.UserService;
import com.org.konnarpc.bootstrap.ProviderBootstrap;
import com.org.konnarpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于测试负载均衡新憎的客户端
 */
public class EasyProviderExample2 {

    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
