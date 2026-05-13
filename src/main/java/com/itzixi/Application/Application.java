package com.itzixi.Application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot 启动类。
 * 负责启动应用上下文，并扫描项目中的组件与 Mapper 接口。
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.itzixi"})
@MapperScan("com.itzixi.mapper")
public class Application {

    /**
     * 程序入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
