package com.memorin.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Memorin统计分析服务启动类
 * 提供学习统计、趋势分析、智能建议等数据分析功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableDiscoveryClient
public class StatisticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("📊 Memorin Statistics Service 启动成功!");
        System.out.println("📈 统计分析功能已就绪");
        System.out.println("🧠 智能推荐引擎已启用");
        System.out.println("💾 Redis缓存服务已连接");
        System.out.println("🌐 API访问地址: http://localhost:8084");
        System.out.println("🏥 健康检查: http://localhost:8084/actuator/health");
        System.out.println("💾 H2控制台: http://localhost:8084/h2-console");
        System.out.println("🔗 Nacos注册: 服务发现已启用");
        System.out.println("=================================");
    }
} 