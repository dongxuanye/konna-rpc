package com.org.konnarpc.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EtcdRegistryDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建客户端使用节点
        Client client = Client.builder( ).endpoints("http://127.0.0.1:2379")
                .build( );

        KV kvClient = client.getKVClient( );
        ByteSequence key = ByteSequence.from("test_key".getBytes( ));
        ByteSequence value = ByteSequence.from("test_value".getBytes( ));

        // 测试设置值
        kvClient.put(key, value).get();
        
        // 获得一个异步的响应结果
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        // 获得响应结果
        GetResponse response = getFuture.get( );

        // 删除这个key
        kvClient.delete(key).get();

    }

}
