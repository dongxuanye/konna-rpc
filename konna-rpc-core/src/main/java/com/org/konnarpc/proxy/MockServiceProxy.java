package com.org.konnarpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * mock 服务代理（JDK动态代理）
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    /**
     *
     *
     * mock 服务代理
     * @param proxy 代理对象
     * @param method  方法
     * @param args 参数
     * @return 返回结果
     * @throws Throwable 抛出异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType( );
        log.info("mock invoke {}",method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 获取默认值
     * @param type 基础类型
     * @return 对象
     */
    private Object getDefaultObject(Class<?> type){
        // 基本类型
        if (type.isPrimitive()){
            if (type == boolean.class){
                return false;
            } else if (type == short.class) {
                return (short)0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }
        }
        // 对象类型
        return null;
    }
}
