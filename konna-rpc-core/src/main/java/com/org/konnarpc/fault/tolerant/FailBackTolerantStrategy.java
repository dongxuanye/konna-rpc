package com.org.konnarpc.fault.tolerant;

import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.proxy.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 降级到其他服务 - 容错策略
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    /**
     * 这里直接返回mock数据模拟降级服务
     *
     * @param context 上下文
     * @param e       异常
     * @return RpcResponse
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        RpcRequest rpcRequest = (RpcRequest) context.getOrDefault("rpcRequest", null);
        if (rpcRequest == null){
            log.info("请求是空的，降不了级呀！");
            throw new RuntimeException(e.getMessage());
        }
        try {
            Class<?> serviceClass = Class.forName(rpcRequest.getServiceName( ));
            // 模拟一波接口调用
            Object mockProxy = ServiceProxyFactory.getMockProxy(serviceClass);
            Method method = mockProxy.getClass( ).getMethod(rpcRequest.getMethodName( ), rpcRequest.getParameterTypes( ));
            Object result = method.invoke(mockProxy, rpcRequest.getArgs( ));

            // 返回结果
            RpcResponse rpcResponse = new RpcResponse( );
            rpcResponse.setData(result);
            rpcResponse.setDataType(method.getReturnType( ));
            rpcResponse.setMessage("Fail Back Tolerant Strategy!");
            log.info("降级到其他服务 mock服务中或者返回404");
            return rpcResponse;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            log.info("类名/方法名/返回结果都找不到完犊子了");
            throw new RuntimeException(ex);
        }
    }
}
