package com.org.examplespringbootprovider;

import com.org.example.common.model.User;
import com.org.example.common.service.UserService;
import com.org.konnarpc.spring.boot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}