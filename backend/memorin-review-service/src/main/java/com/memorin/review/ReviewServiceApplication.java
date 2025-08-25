package com.memorin.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Memorin复习服务启动类
 * 提供SM-2算法、复习会话管理、错题管理等复习相关功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
public class ReviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("🚀 Memorin Review Service 启动成功!");
        System.out.println("🧠 SM-2算法服务已就绪");
        System.out.println("📚 复习管理功能已启用");
        System.out.println("🌐 API访问地址: http://localhost:8083");
        System.out.println("📋 测试接口: http://localhost:8083/api/review/algorithm/test");
        System.out.println("🔗 Nacos注册: 服务发现已启用");
        System.out.println("=================================");
    }
} 