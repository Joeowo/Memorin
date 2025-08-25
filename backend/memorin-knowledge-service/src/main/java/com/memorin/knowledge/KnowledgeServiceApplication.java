package com.memorin.knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Memorin知识管理服务主启动类
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@SpringBootApplication
@EnableDiscoveryClient
public class KnowledgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("Memorin知识管理服务启动成功！");
        System.out.println("端口: 8082");
        System.out.println("健康检查: http://localhost:8082/actuator/health");
        System.out.println("H2控制台: http://localhost:8082/h2-console");
        System.out.println("API测试: http://localhost:8082/api/test/info");
        System.out.println("Nacos注册: 服务发现已启用");
        System.out.println("=================================");
    }
} 