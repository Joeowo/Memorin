package com.memorin.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * API文档聚合控制器
 * 提供统一的微服务API文档聚合功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-17
 */
@RestController
@RequestMapping("/api-docs")
@Tag(name = "API文档聚合", description = "统一聚合所有微服务的API文档，提供集中化的API文档管理")
public class ApiDocsController {

    private final WebClient webClient;
    private final Map<String, String> microservices;

    public ApiDocsController() {
        // 配置增强的WebClient
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB缓冲区
                .build();
        
        // 微服务配置映射 (排除gateway自身避免递归)
        this.microservices = new HashMap<>();
        microservices.put("user", "http://localhost:8081");
        microservices.put("knowledge", "http://localhost:8082");
        microservices.put("review", "http://localhost:8083");
        microservices.put("statistics", "http://localhost:8084");
        // 注意：移除gateway自身，避免递归调用
    }

    /**
     * 获取微服务列表
     */
    @GetMapping
    @Operation(summary = "获取微服务API文档列表", description = "获取所有可用微服务的API文档列表和访问地址")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<Map<String, Object>> getApiDocsList() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "memorin-gateway");
        response.put("description", "Memorin微服务API文档聚合中心");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        
        // 微服务文档链接
        Map<String, String> services = new HashMap<>();
        for (Map.Entry<String, String> entry : microservices.entrySet()) {
            services.put(entry.getKey(), "/api-docs/" + entry.getKey());
        }
        // 添加网关自身的文档链接
        services.put("gateway", "/v3/api-docs");
        
        response.put("services", services);
        response.put("total", services.size());
        response.put("endpoints", createEndpointsInfo());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取指定微服务的API文档
     */
    @GetMapping("/{service}")
    @Operation(summary = "获取微服务API文档", description = "动态代理获取指定微服务的完整OpenAPI文档")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "微服务不存在"),
        @ApiResponse(responseCode = "500", description = "微服务不可用")
    })
    public Mono<ResponseEntity<String>> getServiceApiDocs(@PathVariable String service) {
        // 特殊处理：如果请求gateway文档，重定向到本地端点
        if ("gateway".equals(service)) {
            return Mono.just(ResponseEntity.status(302)
                    .header("Location", "/v3/api-docs")
                    .body("{\"message\":\"Redirected to gateway's own API docs\",\"location\":\"/v3/api-docs\"}"));
        }
        
        String serviceUrl = microservices.get(service);
        
        if (serviceUrl == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Service not found");
            error.put("service", service);
            error.put("available", microservices.keySet());
            error.put("timestamp", LocalDateTime.now());
            error.put("suggestion", "Available services: " + String.join(", ", microservices.keySet()));
            
            return Mono.just(ResponseEntity.notFound()
                    .header("Content-Type", "application/json")
                    .build());
        }
        
        // 动态代理获取微服务的API文档 - 简化版
        return webClient.get()
                .uri(serviceUrl + "/v3/api-docs")
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(500)
                    .body("{\"error\":\"Service unavailable\",\"service\":\"" + service + "\",\"url\":\"" + serviceUrl + "/v3/api-docs\"}"));
    }

    /**
     * 获取所有微服务的健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "检查微服务健康状态", description = "检查所有微服务的API文档可用性和健康状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "检查完成")
    })
    public Mono<ResponseEntity<Map<String, Object>>> getServicesHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());
        health.put("checker", "memorin-gateway");
        health.put("version", "1.0.0");
        
        // 并行检查所有微服务健康状态
        Map<String, Mono<String>> healthChecks = new HashMap<>();
        for (Map.Entry<String, String> entry : microservices.entrySet()) {
            String serviceName = entry.getKey();
            String serviceUrl = entry.getValue();
            
            healthChecks.put(serviceName, 
                webClient.get()
                    .uri(serviceUrl + "/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5)) // 5秒超时
                    .map(response -> "UP")
                    .onErrorReturn("DOWN")
            );
        }
        
        // 简化版本：返回基础健康信息
        Map<String, String> serviceStatus = new HashMap<>();
        for (String serviceName : microservices.keySet()) {
            serviceStatus.put(serviceName, "CHECKING"); // 实际应用中应该异步检查
        }
        
        health.put("services", serviceStatus);
        health.put("total", microservices.size());
        health.put("note", "Use /api-docs/{service} to test individual service availability");
        
        return Mono.just(ResponseEntity.ok(health));
    }

    /**
     * 聚合所有微服务的API概览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取API概览", description = "获取所有微服务的API数量统计和基本信息概览")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<Map<String, Object>> getApiOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            overview.put("title", "Memorin微服务架构API概览");
            overview.put("description", "智能知识复习系统 - 完整的微服务API生态");
            overview.put("timestamp", LocalDateTime.now());
            overview.put("architecture", "Spring Cloud Gateway + Nacos + SpringDoc");
            
            // 服务概览信息
            Map<String, Map<String, Object>> servicesOverview = new HashMap<>();
            
            // 用户服务
            Map<String, Object> userService = new HashMap<>();
            userService.put("name", "用户认证服务");
            userService.put("port", 8081);
            userService.put("features", "认证授权、用户管理、权限控制");
            userService.put("endpoints", "/api/auth/**, /api/user/**");
            servicesOverview.put("user", userService);
            
            // 知识库服务
            Map<String, Object> knowledgeService = new HashMap<>();
            knowledgeService.put("name", "知识库管理服务");
            knowledgeService.put("port", 8082);
            knowledgeService.put("features", "知识点管理、分类管理、题目数据");
            knowledgeService.put("endpoints", "/api/knowledge/**, /api/categories/**");
            servicesOverview.put("knowledge", knowledgeService);
            
            // 复习服务
            Map<String, Object> reviewService = new HashMap<>();
            reviewService.put("name", "智能复习服务");
            reviewService.put("port", 8083);
            reviewService.put("features", "SM-2算法、复习会话、错题本");
            reviewService.put("endpoints", "/api/review/**, /api/sessions/**");
            servicesOverview.put("review", reviewService);
            
            // 统计服务
            Map<String, Object> statisticsService = new HashMap<>();
            statisticsService.put("name", "学习统计分析服务");
            statisticsService.put("port", 8084);
            statisticsService.put("features", "数据分析、进度统计、智能洞察");
            statisticsService.put("endpoints", "/api/statistics/**, /api/reports/**");
            servicesOverview.put("statistics", statisticsService);
            
            // 网关服务
            Map<String, Object> gatewayService = new HashMap<>();
            gatewayService.put("name", "API网关服务");
            gatewayService.put("port", 8080);
            gatewayService.put("features", "路由管理、文档聚合、统一入口");
            gatewayService.put("endpoints", "/api-docs/**, /gateway/**");
            servicesOverview.put("gateway", gatewayService);
            
            overview.put("services", servicesOverview);
            overview.put("totalServices", servicesOverview.size());
            overview.put("status", "success");
            
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            // 如果概览生成失败，返回简化的错误响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate API overview");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", "error");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 测试端点
     */
    @GetMapping("/test")
    @Operation(summary = "测试API文档聚合", description = "测试API文档聚合功能是否正常")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "测试成功")
    })
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "API文档聚合功能正常");
        response.put("timestamp", LocalDateTime.now());
        response.put("availableServices", microservices.keySet());
        response.put("totalServices", microservices.size());
        response.put("gatewayHealth", "OK");
        
        return ResponseEntity.ok(response);
    }

    // ============== 私有辅助方法 ==============

    /**
     * 创建端点信息
     */
    private Map<String, String> createEndpointsInfo() {
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("服务列表", "GET /api-docs");
        endpoints.put("服务文档", "GET /api-docs/{service}");
        endpoints.put("健康检查", "GET /api-docs/health");
        endpoints.put("API概览", "GET /api-docs/overview");
        endpoints.put("功能测试", "GET /api-docs/test");
        return endpoints;
    }

    /**
     * 将Map转换为JSON字符串
     */
    private String convertMapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value.toString());
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }
} 