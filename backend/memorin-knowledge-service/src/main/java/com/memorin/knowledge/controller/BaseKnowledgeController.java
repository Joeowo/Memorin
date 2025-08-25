package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateBaseKnowledgeRequest;
import com.memorin.knowledge.dto.request.UpdateBaseKnowledgeRequest;
import com.memorin.knowledge.dto.response.BaseKnowledgeResponse;
import com.memorin.knowledge.service.BaseKnowledgeService;
import com.memorin.knowledge.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础知识点管理API控制器
 * 提供知识点的增删改查、搜索、统计等RESTful接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/knowledge")
@Validated
public class BaseKnowledgeController {

    private static final Logger logger = LoggerFactory.getLogger(BaseKnowledgeController.class);

    @Autowired
    private BaseKnowledgeService baseKnowledgeService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 创建新知识点
     * POST /api/knowledge
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createKnowledge(
            @Valid @RequestBody CreateBaseKnowledgeRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            BaseKnowledgeResponse knowledge = baseKnowledgeService.createKnowledge(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "知识点创建成功");
            response.put("data", knowledge);
            
            logger.info("用户 {} 成功创建知识点: {}", userId, knowledge.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("创建知识点失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("创建知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "创建知识点失败");
        }
    }

    /**
     * 更新知识点
     * PUT /api/knowledge/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateKnowledge(
            @PathVariable @NotBlank(message = "知识点ID不能为空") String id,
            @Valid @RequestBody UpdateBaseKnowledgeRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            BaseKnowledgeResponse knowledge = baseKnowledgeService.updateKnowledge(id, request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "知识点更新成功");
            response.put("data", knowledge);
            
            logger.info("用户 {} 成功更新知识点: {}", userId, knowledge.getId());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("更新知识点失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("更新知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "更新知识点失败");
        }
    }

    /**
     * 删除知识点
     * DELETE /api/knowledge/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteKnowledge(
            @PathVariable @NotBlank(message = "知识点ID不能为空") String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            baseKnowledgeService.deleteKnowledge(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "知识点删除成功");
            
            logger.info("用户 {} 成功删除知识点: {}", userId, id);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("删除知识点失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("删除知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "删除知识点失败");
        }
    }

    /**
     * 获取单个知识点详情
     * GET /api/knowledge/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getKnowledgeById(
            @PathVariable @NotBlank(message = "知识点ID不能为空") String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            BaseKnowledgeResponse knowledge = baseKnowledgeService.getKnowledgeById(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledge);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取知识点详情失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("获取知识点详情时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点详情失败");
        }
    }

    /**
     * 获取知识点列表
     * GET /api/knowledge
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllKnowledge(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getAllKnowledge(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取知识点列表时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点列表失败");
        }
    }

    /**
     * 根据分类获取知识点
     * GET /api/knowledge/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getKnowledgeByCategory(
            @PathVariable @NotBlank(message = "分类ID不能为空") String categoryId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getKnowledgeByCategory(userId, categoryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("categoryId", categoryId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据分类获取知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点失败");
        }
    }

    /**
     * 根据题型获取知识点
     * GET /api/knowledge/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getKnowledgeByType(
            @PathVariable @NotBlank(message = "题型不能为空") String type,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getKnowledgeByType(userId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("type", type);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据题型获取知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点失败");
        }
    }

    /**
     * 根据难度获取知识点
     * GET /api/knowledge/difficulty/{difficulty}
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Map<String, Object>> getKnowledgeByDifficulty(
            @PathVariable @Min(value = 1, message = "难度等级不能小于1") @Max(value = 5, message = "难度等级不能大于5") Integer difficulty,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getKnowledgeByDifficulty(userId, difficulty);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("difficulty", difficulty);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据难度获取知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点失败");
        }
    }

    /**
     * 根据状态获取知识点
     * GET /api/knowledge/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getKnowledgeByStatus(
            @PathVariable @NotBlank(message = "状态不能为空") String status,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getKnowledgeByStatus(userId, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("status", status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据状态获取知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点失败");
        }
    }

    /**
     * 分页获取知识点列表
     * GET /api/knowledge/page
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getKnowledgeWithPagination(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            Page<BaseKnowledgeResponse> knowledgePage = baseKnowledgeService.getKnowledgeWithPagination(userId, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgePage.getContent());
            response.put("pagination", createPaginationInfo(knowledgePage));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("分页获取知识点列表时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点列表失败");
        }
    }

    /**
     * 搜索知识点
     * GET /api/knowledge/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchKnowledge(
            @RequestParam @NotBlank(message = "搜索关键词不能为空") 
            @Size(min = 1, max = 100, message = "搜索关键词长度必须在1-100个字符之间") String keyword,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.searchKnowledge(userId, keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("搜索知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "搜索知识点失败");
        }
    }

    /**
     * 根据标签获取知识点
     * GET /api/knowledge/tag
     */
    @GetMapping("/tag")
    public ResponseEntity<Map<String, Object>> getKnowledgeByTag(
            @RequestParam @NotBlank(message = "标签不能为空") String tag,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getKnowledgeByTag(userId, tag);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("tag", tag);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据标签获取知识点时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取知识点失败");
        }
    }

    /**
     * 获取知识点统计信息
     * GET /api/knowledge/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getKnowledgeStatistics(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            Map<String, Object> statistics = baseKnowledgeService.getKnowledgeStatistics(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取知识点统计信息时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取统计信息失败");
        }
    }

    /**
     * 从JWT令牌中提取用户ID
     */
    private String extractUserIdFromToken(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("缺少认证信息");
        }
        
        String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
        if (token == null) {
            throw new IllegalArgumentException("无效的认证格式");
        }
        
        if (!jwtTokenUtil.validateToken(token)) {
            throw new IllegalArgumentException("认证信息已过期或无效");
        }
        
        String userId = jwtTokenUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new IllegalArgumentException("无法获取用户信息");
        }
        
        return userId;
    }

    /**
     * 创建错误响应
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 创建分页信息
     */
    private Map<String, Object> createPaginationInfo(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page.getNumber());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("size", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        pagination.put("isFirst", page.isFirst());
        pagination.put("isLast", page.isLast());
        
        return pagination;
    }
} 