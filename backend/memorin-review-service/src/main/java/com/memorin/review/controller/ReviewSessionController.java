package com.memorin.review.controller;

import com.memorin.review.dto.request.CreateReviewSessionRequest;
import com.memorin.review.dto.request.SubmitAnswerRequest;
import com.memorin.review.dto.response.ReviewSessionResponse;
import com.memorin.review.service.ReviewSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 复习会话REST API控制器
 * 提供复习会话管理的HTTP接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/review/sessions")
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "复习会话管理", description = "SM-2算法复习会话相关API，包括会话创建、进度管理、答题提交等功能")
public class ReviewSessionController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewSessionController.class);

    @Autowired
    private ReviewSessionService reviewSessionService;

    /**
     * 创建复习会话
     * POST /api/review/sessions
     */
    @PostMapping
    @Operation(summary = "创建复习会话", description = "根据用户需求创建个性化的复习会话，支持多种复习模式")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "复习会话创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务内部错误")
    })
    public ResponseEntity<Map<String, Object>> createSession(
            @Valid @RequestBody CreateReviewSessionRequest request,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("创建复习会话请求，用户ID: {}, 复习模式: {}", userId, request.getReviewMode());
        
        try {
            ReviewSessionResponse response = reviewSessionService.createSession(request, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "复习会话创建成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            logger.info("复习会话创建成功，会话ID: {}", response.getId());
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("复习会话创建失败，参数错误: {}", e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "参数错误: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResult);
            
        } catch (Exception e) {
            logger.error("复习会话创建失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 获取会话详情
     * GET /api/review/sessions/{sessionId}
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSession(
            @PathVariable String sessionId,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("获取会话详情请求，会话ID: {}, 用户ID: {}", sessionId, userId);
        
        try {
            ReviewSessionResponse response = reviewSessionService.getSession(sessionId, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取会话详情成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取会话详情失败: {}", e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("获取会话详情失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 启动会话
     * PUT /api/review/sessions/{sessionId}/start
     */
    @PutMapping("/{sessionId}/start")
    public ResponseEntity<Map<String, Object>> startSession(
            @PathVariable String sessionId,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("启动会话请求，会话ID: {}, 用户ID: {}", sessionId, userId);
        
        try {
            ReviewSessionResponse response = reviewSessionService.startSession(sessionId, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会话启动成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            logger.info("会话启动成功，会话ID: {}", sessionId);
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("会话启动失败: {}", e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResult);
            
        } catch (IllegalStateException e) {
            logger.warn("会话状态错误: {}", e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResult);
            
        } catch (Exception e) {
            logger.error("会话启动失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 暂停会话
     * PUT /api/review/sessions/{sessionId}/pause
     */
    @PutMapping("/{sessionId}/pause")
    public ResponseEntity<Map<String, Object>> pauseSession(
            @PathVariable String sessionId,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("暂停会话请求，会话ID: {}, 用户ID: {}", sessionId, userId);
        
        try {
            ReviewSessionResponse response = reviewSessionService.pauseSession(sessionId, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会话暂停成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return handleSessionOperationError(e, "会话暂停失败");
        }
    }

    /**
     * 恢复会话
     * PUT /api/review/sessions/{sessionId}/resume
     */
    @PutMapping("/{sessionId}/resume")
    public ResponseEntity<Map<String, Object>> resumeSession(
            @PathVariable String sessionId,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("恢复会话请求，会话ID: {}, 用户ID: {}", sessionId, userId);
        
        try {
            ReviewSessionResponse response = reviewSessionService.resumeSession(sessionId, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会话恢复成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return handleSessionOperationError(e, "会话恢复失败");
        }
    }

    /**
     * 完成会话
     * PUT /api/review/sessions/{sessionId}/complete
     */
    @PutMapping("/{sessionId}/complete")
    public ResponseEntity<Map<String, Object>> completeSession(
            @PathVariable String sessionId,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("完成会话请求，会话ID: {}, 用户ID: {}", sessionId, userId);
        
        try {
            ReviewSessionResponse response = reviewSessionService.completeSession(sessionId, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会话完成成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return handleSessionOperationError(e, "会话完成失败");
        }
    }

    /**
     * 取消会话
     * PUT /api/review/sessions/{sessionId}/cancel
     */
    @PutMapping("/{sessionId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelSession(
            @PathVariable String sessionId,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("取消会话请求，会话ID: {}, 用户ID: {}", sessionId, userId);
        
        try {
            ReviewSessionResponse response = reviewSessionService.cancelSession(sessionId, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会话取消成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return handleSessionOperationError(e, "会话取消失败");
        }
    }

    /**
     * 提交答案
     * POST /api/review/sessions/{sessionId}/submit
     */
    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<Map<String, Object>> submitAnswer(
            @PathVariable String sessionId,
            @Valid @RequestBody SubmitAnswerRequest request,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("提交答案请求，会话ID: {}, 用户ID: {}, 知识点ID: {}", 
                   sessionId, userId, request.getKnowledgePointId());
        
        try {
            // 确保请求中的sessionId与路径中的一致
            request.setSessionId(sessionId);
            
            ReviewSessionResponse response = reviewSessionService.submitAnswer(request, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "答案提交成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            logger.info("答案提交成功，会话ID: {}, 进度: {}/{}", 
                       sessionId, response.getCompletedCount(), response.getTotalQuestions());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return handleSessionOperationError(e, "答案提交失败");
        }
    }

    /**
     * 获取用户会话列表
     * GET /api/review/sessions?status=xxx&limit=10
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserSessions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer limit,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("获取用户会话列表，用户ID: {}, 状态过滤: {}, 限制数量: {}", userId, status, limit);
        
        try {
            List<ReviewSessionResponse> sessions = reviewSessionService.getUserSessions(userId, status, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取会话列表成功");
            result.put("data", sessions);
            result.put("count", sessions.size());
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("获取用户会话列表失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 获取用户活跃会话
     * GET /api/review/sessions/active
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveSessions(
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("获取用户活跃会话，用户ID: {}", userId);
        
        try {
            List<ReviewSessionResponse> sessions = reviewSessionService.getActiveSessions(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取活跃会话成功");
            result.put("data", sessions);
            result.put("count", sessions.size());
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("获取用户活跃会话失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "服务器内部错误");
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * 删除会话
     * DELETE /api/review/sessions/{sessionId}
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Map<String, Object>> deleteSession(
            @PathVariable String sessionId,
            @RequestHeader(value = "User-Id", defaultValue = "USER_001") String userId) {
        
        logger.info("删除会话请求，会话ID: {}, 用户ID: {}", sessionId, userId);
        
        try {
            reviewSessionService.deleteSession(sessionId, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会话删除成功");
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return handleSessionOperationError(e, "会话删除失败");
        }
    }

    /**
     * 测试接口 - 创建测试会话
     * GET /api/review/sessions/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> createTestSession() {
        logger.info("创建测试会话");
        
        try {
            // 创建测试请求
            CreateReviewSessionRequest request = new CreateReviewSessionRequest();
            request.setReviewMode("smart-review");
            request.setTargetQuestionCount(10);
            request.setSortOrder("smart");
            request.setMaxDurationMinutes(30);
            request.setNotes("测试会话");
            
            ReviewSessionResponse response = reviewSessionService.createSession(request, "USER_001");
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "测试会话创建成功");
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("创建测试会话失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "测试失败: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    // ============== 私有辅助方法 ==============

    /**
     * 处理会话操作错误
     */
    private ResponseEntity<Map<String, Object>> handleSessionOperationError(Exception e, String operation) {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("success", false);
        errorResult.put("timestamp", System.currentTimeMillis());
        
        if (e instanceof IllegalArgumentException) {
            logger.warn("{}: {}", operation, e.getMessage());
            errorResult.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        } else if (e instanceof IllegalStateException) {
            logger.warn("{}，状态错误: {}", operation, e.getMessage());
            errorResult.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        } else {
            logger.error(operation, e);
            errorResult.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
} 