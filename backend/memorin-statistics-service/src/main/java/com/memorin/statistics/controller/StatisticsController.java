package com.memorin.statistics.controller;

import com.memorin.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计数据控制器
 * 提供用户学习统计、趋势分析等数据接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/statistics")
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "学习统计分析", description = "用户学习数据统计分析API，提供多维度学习数据洞察和可视化报表")
public class StatisticsController {
    
    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取用户学习概览统计
     * 
     * @param userId 用户ID
     * @param days 统计天数，默认30天
     * @return 用户学习概览数据
     */
    @GetMapping("/overview/{userId}")
    @Operation(summary = "获取用户学习概览", description = "获取指定用户在指定时间范围内的学习统计概览数据，包括学习时长、复习次数、正确率等关键指标")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取学习概览成功"),
        @ApiResponse(responseCode = "400", description = "参数验证失败"),
        @ApiResponse(responseCode = "500", description = "服务内部错误")
    })
    public ResponseEntity<Map<String, Object>> getUserOverviewStatistics(
            @PathVariable String userId,
            @RequestParam(defaultValue = "30") int days) {
        
        try {
            log.info("[API] 获取用户学习概览: userId={}, days={}", userId, days);
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("[API] 用户ID参数无效: userId={}", userId);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (days <= 0 || days > 365) {
                log.warn("[API] 天数参数无效: days={}", days);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "天数必须在1-365之间");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // 调用Service层获取统计数据
            Map<String, Object> statistics = statisticsService.getUserOverviewStatistics(userId, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            response.put("message", "获取学习概览成功");
            response.put("timestamp", LocalDateTime.now());
            
            log.info("[API] 获取用户学习概览成功: userId={}", userId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("[API] 参数验证失败: userId={}, error={}", userId, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "参数验证失败: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("[API] 获取学习概览失败: userId={}, error={}", userId, e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取学习概览失败: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 