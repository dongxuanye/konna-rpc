package com.org.examplespringbootprovider;

import com.org.konnarpc.spring.boot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动rpc配置
 */
@EnableRpc
@SpringBootApplication
public class ExampleSpringbootProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootProviderApplication.class, args);
    }

}
