package com.org.konnarpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.org.konnarpc.RpcApplication;
import com.org.konnarpc.config.RpcConfig;
import com.org.konnarpc.constant.RpcConstant;
import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.model.ServiceMetaInfo;
import com.org.konnarpc.registry.Registry;
import com.org.konnarpc.registry.RegistryFactory;
import com.org.konnarpc.serializer.JdkSerializer;
import com.org.konnarpc.serializer.Serializer;
import com.org.konnarpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理类(jdk动态代理)
 * Object proxy, Method method, Object[] args
 * 与静态方法不同之处在于构造rpc请求参数不同
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * 调用方法
     * @param proxy 代理对象
     * @param method 方法
     * @param args 参数
     * @return 返回调用对象
     * @throws Throwable 抛出异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass( ).getName( );
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder( )
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build( );
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            RpcConfig rpcConfig = RpcApplication.getRpcConfig( );
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig( ).getRegistry( ));
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo( );
            // 设置元信息的名字和版本号
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            // 看看有几个服务地址注册上去了
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey( ));
            // 判断一下是否为空
            if (CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("尚未发现服务");
            }
            // todo 暂时取第一个地址
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            // 发送请求
            try(HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()){
                byte[] result = httpResponse.bodyBytes( );
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData( );
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
