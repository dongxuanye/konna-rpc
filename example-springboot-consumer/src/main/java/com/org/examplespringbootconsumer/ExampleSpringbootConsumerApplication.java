package com.org.examplespringbootconsumer;

import com.org.konnarpc.spring.boot.starter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 不需要启动服务
 */
@EnableRpc(needServer = false)
@SpringBootApplication
public class ExampleSpringbootConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
	}

}
