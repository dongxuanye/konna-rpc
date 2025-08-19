package com.org.example.consumer;

import com.org.example.common.model.User;
import com.org.example.common.service.UserService;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.proxy.ServiceProxyFactory;
import com.org.konnarpc.utils.ConfigUtils;

/**
 * author: 简易的消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) throws InterruptedException {
        // 启动时加载配置类
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
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

//        long number = userService.getNumber( );
//        System.out.println("number:"+number);

//        // 睡眠10秒
//        Thread.sleep( 10000L);
//        newUser = userService.getUser(user);
//        if (newUser != null){
//            System.out.println("消费者一端"+newUser.getName());
//        }else {
//            System.out.println("user == null" );
//        }
//
//        // 睡眠10秒
//        Thread.sleep( 10000L);
//        newUser = userService.getUser(user);
//        if (newUser != null){
//            System.out.println("消费者一端"+newUser.getName());
//        }else {
//            System.out.println("user == null" );
//        }
//
//        // 睡眠10秒
//        Thread.sleep( 10000L);
//        newUser = userService.getUser(user);
//        if (newUser != null){
//            System.out.println("消费者一端"+newUser.getName());
//        }else {
//            System.out.println("user == null" );
//        }
    }

    public static UserService getStaticService(){
        return new UserServiceProxy();
    }

    // 动态代理对象
    public static UserService getDynamicService(){
        return ServiceProxyFactory.getProxy(UserService.class);
    }

}
