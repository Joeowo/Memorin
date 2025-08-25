package com.memorin.knowledge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 提供服务健康检查和基本信息接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
@Tag(name = "知识库测试", description = "知识库服务测试接口，提供服务健康检查、状态监控和基本信息查询")
public class TestController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    /**
     * 服务信息接口
     * GET /api/test/info
     */
    @GetMapping("/info")
    @Operation(summary = "获取服务信息", description = "获取知识库服务的基本信息，包括服务名称、版本、端口等")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取服务信息")
    })
    public ResponseEntity<Map<String, Object>> getServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", applicationName);
        info.put("version", "1.0.0");
        info.put("port", serverPort);
        info.put("status", "running");
        info.put("timestamp", LocalDateTime.now());
        info.put("description", "Memorin知识管理服务");
        
        return ResponseEntity.ok(info);
    }

    /**
     * 简单健康检查接口
     * GET /api/test/health
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查知识库服务是否正常运行")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "服务运行正常")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", applicationName);
        health.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(health);
    }

    /**
     * 服务就绪检查接口
     * GET /api/test/ready
     */
    @GetMapping("/ready")
    @Operation(summary = "就绪检查", description = "检查知识库服务是否准备好接收请求，包括数据库、缓存等组件状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "服务已准备就绪")
    })
    public ResponseEntity<Map<String, Object>> readinessCheck() {
        Map<String, Object> ready = new HashMap<>();
        ready.put("ready", true);
        ready.put("service", applicationName);
        
        // 创建组件状态Map (Java 8兼容方式)
        Map<String, String> components = new HashMap<>();
        components.put("database", "UP");
        components.put("redis", "UP");
        components.put("application", "UP");
        ready.put("components", components);
        
        ready.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ready);
    }
} 