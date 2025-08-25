package com.memorin.review.controller;

import com.memorin.review.dto.request.ReviewAlgorithmRequest;
import com.memorin.review.dto.response.ReviewAlgorithmResponse;
import com.memorin.review.service.ReviewAlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SM-2算法REST API控制器
 * 提供复习算法计算的HTTP接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/review/algorithm")
@Validated
@CrossOrigin(origins = "*")
public class ReviewAlgorithmController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewAlgorithmController.class);

    @Autowired
    private ReviewAlgorithmService reviewAlgorithmService;

    /**
     * 计算单个知识点的下次复习时间
     * POST /api/review/algorithm/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateNextReview(
            @Valid @RequestBody ReviewAlgorithmRequest request) {
        
        logger.info("接收SM-2算法计算请求: {}", request);
        
        try {
            // 参数验证
            reviewAlgorithmService.validateParameters(
                request.getEaseFactor(), request.getInterval(), request.getQuality());
            
            // 执行算法计算
            ReviewAlgorithmResponse response = reviewAlgorithmService.calculateNextReview(request);
            
            // 构建成功响应
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "SM-2算法计算成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            logger.info("SM-2算法计算成功，返回结果: {}", response);
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("SM-2算法计算参数错误: {}", e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "参数错误: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResult);
            
        } catch (Exception e) {
            logger.error("SM-2算法计算失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 批量计算多个知识点的复习时间
     * POST /api/review/algorithm/batch-calculate
     */
    @PostMapping("/batch-calculate")
    public ResponseEntity<Map<String, Object>> batchCalculateNextReview(
            @Valid @RequestBody List<ReviewAlgorithmRequest> requests) {
        
        logger.info("接收批量SM-2算法计算请求，处理{}个知识点", requests.size());
        
        try {
            // 批量参数验证
            for (ReviewAlgorithmRequest request : requests) {
                reviewAlgorithmService.validateParameters(
                    request.getEaseFactor(), request.getInterval(), request.getQuality());
            }
            
            // 执行批量计算
            List<ReviewAlgorithmResponse> responses = reviewAlgorithmService.batchCalculateNextReview(requests);
            
            // 构建成功响应
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", String.format("批量计算成功，处理了%d个知识点", responses.size()));
            result.put("data", responses);
            result.put("count", responses.size());
            result.put("timestamp", System.currentTimeMillis());
            
            logger.info("批量SM-2算法计算成功，处理了{}个知识点", responses.size());
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("批量SM-2算法计算参数错误: {}", e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "参数错误: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResult);
            
        } catch (Exception e) {
            logger.error("批量SM-2算法计算失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 获取默认算法参数
     * GET /api/review/algorithm/default-parameters
     */
    @GetMapping("/default-parameters")
    public ResponseEntity<Map<String, Object>> getDefaultParameters() {
        logger.info("获取默认算法参数");
        
        try {
            ReviewAlgorithmResponse defaultParams = reviewAlgorithmService.getDefaultParameters();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取默认参数成功");
            result.put("data", defaultParams);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("获取默认算法参数失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 计算掌握度评估
     * GET /api/review/algorithm/mastery-level
     */
    @GetMapping("/mastery-level")
    public ResponseEntity<Map<String, Object>> calculateMasteryLevel(
            @RequestParam Double easeFactor,
            @RequestParam Integer reviewCount,
            @RequestParam Integer correctCount) {
        
        logger.info("计算掌握度评估: EF={}, 复习次数={}, 正确次数={}", easeFactor, reviewCount, correctCount);
        
        try {
            Double masteryLevel = reviewAlgorithmService.calculateMasteryLevel(easeFactor, reviewCount, correctCount);
            
            // 构建掌握度数据
            Map<String, Object> masteryData = new HashMap<>();
            masteryData.put("masteryLevel", masteryLevel);
            masteryData.put("masteryPercentage", Math.round(masteryLevel * 100));
            masteryData.put("description", getMasteryDescription(masteryLevel));
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "掌握度计算成功");
            result.put("data", masteryData);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("掌握度计算失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 测试接口 - 验证算法功能
     * GET /api/review/algorithm/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAlgorithm() {
        logger.info("执行SM-2算法测试");
        
        try {
            // 测试不同质量评分的计算结果
            Map<String, Object> testResults = new HashMap<>();
            
            // 测试错误评分 (quality = 1)
            ReviewAlgorithmRequest errorRequest = new ReviewAlgorithmRequest(2.5, 7, 1);
            ReviewAlgorithmResponse errorResponse = reviewAlgorithmService.calculateNextReview(errorRequest);
            
            Map<String, Object> errorTest = new HashMap<>();
            errorTest.put("input", errorRequest);
            errorTest.put("output", errorResponse);
            errorTest.put("description", "错误评分测试：应该大幅降低易度因子，6小时后复习");
            testResults.put("errorTest", errorTest);
            
            // 测试模糊评分 (quality = 2)
            ReviewAlgorithmRequest vagueRequest = new ReviewAlgorithmRequest(2.5, 7, 2);
            ReviewAlgorithmResponse vagueResponse = reviewAlgorithmService.calculateNextReview(vagueRequest);
            
            Map<String, Object> vagueTest = new HashMap<>();
            vagueTest.put("input", vagueRequest);
            vagueTest.put("output", vagueResponse);
            vagueTest.put("description", "模糊评分测试：应该轻微降低易度因子，缩短复习间隔");
            testResults.put("vagueTest", vagueTest);
            
            // 测试正确评分 (quality = 3)
            ReviewAlgorithmRequest correctRequest = new ReviewAlgorithmRequest(2.5, 7, 3);
            ReviewAlgorithmResponse correctResponse = reviewAlgorithmService.calculateNextReview(correctRequest);
            
            Map<String, Object> correctTest = new HashMap<>();
            correctTest.put("input", correctRequest);
            correctTest.put("output", correctResponse);
            correctTest.put("description", "正确评分测试：应该提高易度因子，延长复习间隔");
            testResults.put("correctTest", correctTest);
            
            // 测试默认参数
            ReviewAlgorithmResponse defaultParams = reviewAlgorithmService.getDefaultParameters();
            
            Map<String, Object> defaultTest = new HashMap<>();
            defaultTest.put("output", defaultParams);
            defaultTest.put("description", "默认参数测试：新知识点的初始参数");
            testResults.put("defaultParams", defaultTest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "SM-2算法测试完成");
            result.put("data", testResults);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("SM-2算法测试失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "测试失败: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 获取掌握度描述
     */
    private String getMasteryDescription(Double masteryLevel) {
        if (masteryLevel >= 0.8) {
            return "掌握良好";
        } else if (masteryLevel >= 0.6) {
            return "基本掌握";
        } else if (masteryLevel >= 0.4) {
            return "部分掌握";
        } else if (masteryLevel >= 0.2) {
            return "初步了解";
        } else {
            return "需要加强";
        }
    }
} 