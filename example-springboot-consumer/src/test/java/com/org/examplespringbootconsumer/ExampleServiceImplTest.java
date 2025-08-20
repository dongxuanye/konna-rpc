package com.org.examplespringbootconsumer;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExampleServiceImplTest {

    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    public void testTest() {
        exampleService.test();
    }
}