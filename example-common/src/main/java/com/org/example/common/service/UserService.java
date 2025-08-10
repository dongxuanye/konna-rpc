package com.org.example.common.service;

import com.org.example.common.model.User;

/**
 * author: konna
 * version: 1.0
 */
public interface UserService {

    /**
     * 获取用户信息
     * @param user 用户信息
     * @return 用户
     */
    User getUser(User user);

    /**
     * 用于测试 mock 接口返回值
     *
     * @return 1
     */
    default short getNumber() {
        return 1;
    }
}
