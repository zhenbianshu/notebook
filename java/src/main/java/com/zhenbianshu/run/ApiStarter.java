package com.zhenbianshu.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * created by zbs on 2018/3/20
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.zhenbianshu"})
@ImportResource({"classpath:zbs.xml"})
public class ApiStarter {
    public static void main(String[] args) {
        SpringApplication.run(ApiStarter.class, args);
    }
}
