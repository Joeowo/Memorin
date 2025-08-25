package com.memorin.review.controller;

import com.memorin.review.dto.request.MistakeRequest;
import com.memorin.review.dto.response.MistakeResponse;
import com.memorin.review.entity.MistakeRecord;
import com.memorin.review.service.MistakeManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 错题管理控制器
 * 提供错题记录、查询、分析和解决的REST API
 */
@RestController
@RequestMapping("/api/review/mistakes")
public class MistakeController {

    private static final Logger logger = LoggerFactory.getLogger(MistakeController.class);

    @Autowired
    private MistakeManagementService mistakeManagementService;

    // ======================== 错题记录管理 ========================

    /**
     * 添加或更新错题记录
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addMistake(@Valid @RequestBody MistakeRequest request) {
        logger.info("接收添加错题请求: {}", request);
        
        try {
            MistakeRecord mistakeRecord = mistakeManagementService.addOrUpdateMistake(
                    request.getUserId(),
                    request.getKnowledgePointId(),
                    request.getMistakeReason(),
                    request.getQuestionType(),
                    request.getDifficultyLevel()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "错题记录添加成功");
            response.put("data", MistakeResponse.fromEntity(mistakeRecord));

            logger.info("错题记录添加成功: mistakeId={}", mistakeRecord.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("添加错题记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "添加错题记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 标记错题为已解决
     */
    @PutMapping("/{userId}/{knowledgePointId}/resolve")
    public ResponseEntity<Map<String, Object>> resolveMistake(
            @PathVariable String userId,
            @PathVariable String knowledgePointId) {
        
        logger.info("接收解决错题请求: userId={}, knowledgePointId={}", userId, knowledgePointId);
        
        try {
            boolean success = mistakeManagementService.resolveMistake(userId, knowledgePointId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "错题已标记为解决" : "未找到错题记录");
            
            if (success) {
                logger.info("错题解决成功: userId={}, knowledgePointId={}", userId, knowledgePointId);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("错题解决失败，未找到记录: userId={}, knowledgePointId={}", userId, knowledgePointId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("解决错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "解决错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 重新标记错题为未解决
     */
    @PutMapping("/{userId}/{knowledgePointId}/unresolve")
    public ResponseEntity<Map<String, Object>> unresolveMistake(
            @PathVariable String userId,
            @PathVariable String knowledgePointId) {
        
        logger.info("接收重新标记错题为未解决请求: userId={}, knowledgePointId={}", userId, knowledgePointId);
        
        try {
            boolean success = mistakeManagementService.unresolveMistake(userId, knowledgePointId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "错题已重新标记为未解决" : "未找到错题记录");
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            logger.error("重新标记错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "重新标记错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ======================== 错题查询 ========================

    /**
     * 获取用户的所有错题
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserMistakes(@PathVariable String userId) {
        logger.debug("获取用户所有错题: userId={}", userId);
        
        try {
            List<MistakeRecord> mistakes = mistakeManagementService.getUserMistakes(userId);
            List<MistakeResponse> mistakeResponses = mistakes.stream()
                    .map(MistakeResponse::fromEntity)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", mistakeResponses);
            response.put("total", mistakeResponses.size());

            logger.debug("用户错题查询成功: userId={}, 数量={}", userId, mistakeResponses.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("查询用户错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "查询用户错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取用户的未解决错题
     */
    @GetMapping("/user/{userId}/unresolved")
    public ResponseEntity<Map<String, Object>> getUnresolvedMistakes(@PathVariable String userId) {
        logger.debug("获取用户未解决错题: userId={}", userId);
        
        try {
            List<MistakeRecord> mistakes = mistakeManagementService.getUnresolvedMistakes(userId);
            List<MistakeResponse> mistakeResponses = mistakes.stream()
                    .map(MistakeResponse::fromEntity)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", mistakeResponses);
            response.put("total", mistakeResponses.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("查询未解决错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "查询未解决错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取用户的已解决错题
     */
    @GetMapping("/user/{userId}/resolved")
    public ResponseEntity<Map<String, Object>> getResolvedMistakes(@PathVariable String userId) {
        logger.debug("获取用户已解决错题: userId={}", userId);
        
        try {
            List<MistakeRecord> mistakes = mistakeManagementService.getResolvedMistakes(userId);
            List<MistakeResponse> mistakeResponses = mistakes.stream()
                    .map(MistakeResponse::fromEntity)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", mistakeResponses);
            response.put("total", mistakeResponses.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("查询已解决错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "查询已解决错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取高优先级错题（需要重点复习）
     */
    @GetMapping("/user/{userId}/high-priority")
    public ResponseEntity<Map<String, Object>> getHighPriorityMistakes(
            @PathVariable String userId,
            @RequestParam(defaultValue = "3") Integer threshold) {
        
        logger.debug("获取高优先级错题: userId={}, threshold={}", userId, threshold);
        
        try {
            List<MistakeRecord> mistakes = mistakeManagementService.getHighPriorityMistakes(userId, threshold);
            List<MistakeResponse> mistakeResponses = mistakes.stream()
                    .map(MistakeResponse::fromEntity)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", mistakeResponses);
            response.put("total", mistakeResponses.size());
            response.put("threshold", threshold);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("查询高优先级错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "查询高优先级错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取最近N天的错题
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<Map<String, Object>> getRecentMistakes(
            @PathVariable String userId,
            @RequestParam(defaultValue = "7") Integer days) {
        
        logger.debug("获取最近{}天错题: userId={}", days, userId);
        
        try {
            List<MistakeRecord> mistakes = mistakeManagementService.getRecentMistakes(userId, days);
            List<MistakeResponse> mistakeResponses = mistakes.stream()
                    .map(MistakeResponse::fromEntity)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", mistakeResponses);
            response.put("total", mistakeResponses.size());
            response.put("days", days);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("查询最近错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "查询最近错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ======================== 错题统计 ========================

    /**
     * 获取用户错题统计信息
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getMistakeStatistics(@PathVariable String userId) {
        logger.debug("获取用户错题统计: userId={}", userId);
        
        try {
            Map<String, Object> statistics = mistakeManagementService.getMistakeStatistics(userId);
            MistakeResponse.MistakeStatisticsResponse statsResponse = 
                    MistakeResponse.MistakeStatisticsResponse.fromMap(statistics);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "统计查询成功");
            response.put("data", statsResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("查询错题统计失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "查询错题统计失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ======================== 错题删除 ========================

    /**
     * 删除指定错题记录
     */
    @DeleteMapping("/{userId}/{knowledgePointId}")
    public ResponseEntity<Map<String, Object>> deleteMistake(
            @PathVariable String userId,
            @PathVariable String knowledgePointId) {
        
        logger.info("接收删除错题请求: userId={}, knowledgePointId={}", userId, knowledgePointId);
        
        try {
            boolean success = mistakeManagementService.deleteMistake(userId, knowledgePointId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "错题记录删除成功" : "未找到错题记录");
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            logger.error("删除错题记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "删除错题记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 清理已解决的错题记录
     */
    @DeleteMapping("/user/{userId}/resolved")
    public ResponseEntity<Map<String, Object>> cleanupResolvedMistakes(@PathVariable String userId) {
        logger.info("接收清理已解决错题请求: userId={}", userId);
        
        try {
            mistakeManagementService.cleanupResolvedMistakes(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "已解决的错题记录清理成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("清理已解决错题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "清理已解决错题失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ======================== 测试接口 ========================

    /**
     * 错题管理服务测试接口
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testMistakeService() {
        logger.info("错题管理服务测试接口调用");
        
        try {
            // 创建测试错题记录
            MistakeRecord testMistake = mistakeManagementService.addOrUpdateMistake(
                    "USER_001", 
                    "TEST_KNOWLEDGE_001", 
                    "测试错误原因", 
                    "choice", 
                    "medium"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "错题管理服务测试成功");
            response.put("testMistakeId", testMistake.getId());
            response.put("serviceStatus", "正常运行");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("错题管理服务测试失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "错题管理服务测试失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取指定知识点的错题记录详情
     */
    @GetMapping("/{userId}/{knowledgePointId}")
    public ResponseEntity<Map<String, Object>> getMistakeDetail(
            @PathVariable String userId,
            @PathVariable String knowledgePointId) {
        
        logger.debug("获取错题详情: userId={}, knowledgePointId={}", userId, knowledgePointId);
        
        try {
            Optional<MistakeRecord> mistakeOpt = mistakeManagementService.getMistakeRecord(userId, knowledgePointId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (mistakeOpt.isPresent()) {
                response.put("success", true);
                response.put("message", "查询成功");
                response.put("data", MistakeResponse.fromEntity(mistakeOpt.get()));
            } else {
                response.put("success", false);
                response.put("message", "未找到错题记录");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询错题详情失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "查询错题详情失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 