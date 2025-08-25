package com.memorin.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Memorin API网关控制器
 * 提供网关基本信息和路由管理API
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-17
 */
@RestController
@RequestMapping("/gateway")
@Tag(name = "网关管理", description = "API网关信息和路由管理接口")
public class GatewayController {

    @Autowired
    private RouteLocator routeLocator;

    @Operation(summary = "获取网关信息", description = "获取API网关的基本信息和状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务异常")
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getGatewayInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("serviceName", "memorin-gateway");
        info.put("version", "1.0.0");
        info.put("description", "Memorin智能知识复习系统 - API网关服务");
        info.put("timestamp", LocalDateTime.now());
        info.put("status", "running");
        
        Map<String, String> services = new HashMap<>();
        services.put("用户服务", "http://localhost:8081");
        services.put("知识库服务", "http://localhost:8082");
        services.put("复习服务", "http://localhost:8083");
        services.put("统计服务", "http://localhost:8084");
        info.put("microservices", services);
        
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "获取路由列表", description = "获取当前网关配置的所有路由规则")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/routes")
    public ResponseEntity<Map<String, Object>> getRoutes() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();
        
        List<Map<String, Object>> routeInfos = routes.stream()
                .map(route -> {
                    Map<String, Object> routeInfo = new HashMap<>();
                    routeInfo.put("id", route.getId());
                    routeInfo.put("uri", route.getUri().toString());
                    routeInfo.put("order", route.getOrder());
                    return routeInfo;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRoutes", routeInfos.size());
        result.put("routes", routeInfos);
        result.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "健康检查", description = "检查网关服务健康状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "服务正常")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "memorin-gateway");
        health.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(health);
    }
} 