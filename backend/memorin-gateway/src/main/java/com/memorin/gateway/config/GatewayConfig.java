package com.memorin.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 * 使用代码方式配置路由，确保路径剥离正确工作
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ========== 用户服务路由 (8081) ==========
                
                // 认证相关API
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                
                // 用户管理API
                .route("user-service", r -> r
                        .path("/api/user/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                
                // 用户服务测试API
                .route("user-test-service", r -> r
                        .path("/api/test/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                
                // ========== 知识库服务路由 (8082) ==========
                
                // 知识库管理API
                .route("knowledge-service", r -> r
                        .path("/api/knowledge/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8082"))
                
                // 知识库分类API
                .route("knowledge-category-service", r -> r
                        .path("/api/categories/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8082"))
                
                // ========== 复习服务路由 (8083) ==========
                
                // 复习算法API
                .route("review-algorithm-service", r -> r
                        .path("/api/review/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8083"))
                
                // 错题管理API
                .route("mistakes-service", r -> r
                        .path("/api/mistakes/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8083"))
                
                // 复习会话API
                .route("review-sessions-service", r -> r
                        .path("/api/sessions/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8083"))
                
                // ========== 统计服务路由 (8084) ==========
                
                // 统计分析API
                .route("statistics-service", r -> r
                        .path("/api/statistics/**")
                        .filters(f -> f.stripPrefix(0))  // 不剥离路径，保持完整路径
                        .uri("http://localhost:8084"))
                
                // 报表生成API
                .route("reports-service", r -> r
                        .path("/api/reports/**")
                        .filters(f -> f.stripPrefix(0))  // 不剥离路径，保持完整路径
                        .uri("http://localhost:8084"))
                
                // ========== Actuator监控端点路由 ==========
                
                // 用户服务监控
                .route("user-actuator", r -> r
                        .path("/user/actuator/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "user-service"))
                        .uri("http://localhost:8081"))
                
                // 知识库服务监控
                .route("knowledge-actuator", r -> r
                        .path("/knowledge/actuator/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "knowledge-service"))
                        .uri("http://localhost:8082"))
                
                // 复习服务监控
                .route("review-actuator", r -> r
                        .path("/review/actuator/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "review-service"))
                        .uri("http://localhost:8083"))
                
                // 统计服务监控
                .route("statistics-actuator", r -> r
                        .path("/statistics/actuator/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "statistics-service"))
                        .uri("http://localhost:8084"))
                
                // ========== 网关自身监控 ==========
                
                // 网关健康检查由Spring Boot Actuator自动处理，不需要额外路由配置
                
                .build();
    }
} 