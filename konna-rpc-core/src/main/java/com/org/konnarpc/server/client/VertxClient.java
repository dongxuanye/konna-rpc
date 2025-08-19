package com.org.konnarpc.server.client;

import com.org.konnarpc.model.RpcRequest;
import com.org.konnarpc.model.RpcResponse;
import com.org.konnarpc.model.ServiceMetaInfo;

/**
 * 基于vertx的rpc客户端
 */
public interface VertxClient {

    RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception;

}
