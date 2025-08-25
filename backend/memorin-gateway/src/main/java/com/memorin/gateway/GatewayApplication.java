package com.memorin.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Memorin API网关主启动类
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("=================================");
        System.out.println("Memorin API网关启动成功！");
        System.out.println("端口: 8080");
        System.out.println("健康检查: http://localhost:8080/actuator/health");
        System.out.println("网关路由: http://localhost:8080/actuator/gateway/routes");
        System.out.println("Nacos注册: 服务发现已启用");
        System.out.println("=================================");
    }
} 