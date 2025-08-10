package com.org.example.provider;

import com.org.example.common.model.User;
import com.org.example.common.service.UserService;

/**
 * author: konna
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名为："+user.getName( ));
        return user;
    }
}
