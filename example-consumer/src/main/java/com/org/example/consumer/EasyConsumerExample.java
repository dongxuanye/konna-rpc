package com.org.example.consumer;

import com.org.example.common.model.User;
import com.org.example.common.service.UserService;
import com.org.konnarpc.proxy.ServiceProxyFactory;

/**
 * author: 简易的消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        // todo 需要获取UserService的实现类对象
        UserService userService =  getDynamicService();
        User user = new User( );
        user.setName("konna");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println("消费者一端"+newUser.getName());
        }else {
            System.out.println("user == null" );
        }
    }

    public static UserService getStaticService(){
        return new UserServiceProxy();
    }

    // 动态代理对象
    public static UserService getDynamicService(){
        return ServiceProxyFactory.getProxy(UserService.class);
    }

}
