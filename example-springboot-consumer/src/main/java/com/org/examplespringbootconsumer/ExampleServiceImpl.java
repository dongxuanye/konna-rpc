package com.org.examplespringbootconsumer;

import com.org.example.common.model.User;
import com.org.example.common.service.UserService;
import com.org.konnarpc.spring.boot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;

    /**
     * 测试方法
     */
    public void test() {
        User user = new User();
        user.setName("konna");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}