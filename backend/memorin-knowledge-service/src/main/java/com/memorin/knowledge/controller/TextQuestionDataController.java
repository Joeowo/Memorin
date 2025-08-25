package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateTextQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateTextQuestionDataRequest;
import com.memorin.knowledge.dto.response.TextQuestionDataResponse;
import com.memorin.knowledge.service.TextQuestionDataService;
import com.memorin.knowledge.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本题数据管理REST API控制器
 * 提供文本题数据的完整CRUD操作接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/textquestions")
@Validated
public class TextQuestionDataController {

    private static final Logger logger = LoggerFactory.getLogger(TextQuestionDataController.class);

    @Autowired
    private TextQuestionDataService textQuestionDataService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 从请求中提取用户ID
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
        if (token != null && jwtTokenUtil.validateToken(token)) {
            return jwtTokenUtil.getUserIdFromToken(token);
        }
        throw new IllegalArgumentException("无效的认证令牌");
    }

    /**
     * 创建文本题数据
     * POST /api/textquestions
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTextQuestionData(
            @Valid @RequestBody CreateTextQuestionDataRequest request,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 创建文本题数据", userId);

            TextQuestionDataResponse response = textQuestionDataService.createTextQuestionData(request, userId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(createSuccessResponse("文本题数据创建成功", response));
                
        } catch (IllegalArgumentException e) {
            logger.warn("创建文本题数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("创建失败", e.getMessage()));
        } catch (Exception e) {
            logger.error("创建文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "创建文本题数据失败"));
        }
    }

    /**
     * 更新文本题数据
     * PUT /api/textquestions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTextQuestionData(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody UpdateTextQuestionDataRequest request,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 更新文本题数据 {}", userId, id);

            TextQuestionDataResponse response = textQuestionDataService.updateTextQuestionData(id, request, userId);
            
            return ResponseEntity.ok(createSuccessResponse("文本题数据更新成功", response));
                
        } catch (IllegalArgumentException e) {
            logger.warn("更新文本题数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("更新失败", e.getMessage()));
        } catch (Exception e) {
            logger.error("更新文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "更新文本题数据失败"));
        }
    }

    /**
     * 删除文本题数据
     * DELETE /api/textquestions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTextQuestionData(
            @PathVariable @NotBlank String id,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 删除文本题数据 {}", userId, id);

            textQuestionDataService.deleteTextQuestionData(id, userId);
            
            return ResponseEntity.ok(createSuccessResponse("文本题数据删除成功", null));
                
        } catch (IllegalArgumentException e) {
            logger.warn("删除文本题数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("删除失败", e.getMessage()));
        } catch (Exception e) {
            logger.error("删除文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "删除文本题数据失败"));
        }
    }

    /**
     * 根据ID获取文本题数据详情
     * GET /api/textquestions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataById(
            @PathVariable @NotBlank String id,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 获取文本题数据详情 {}", userId, id);

            TextQuestionDataResponse response = textQuestionDataService.getTextQuestionDataById(id, userId);
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", response));
                
        } catch (IllegalArgumentException e) {
            logger.warn("获取文本题数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("获取失败", e.getMessage()));
        } catch (Exception e) {
            logger.error("获取文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 根据知识点ID获取文本题数据
     * GET /api/textquestions/knowledge/{knowledgeId}
     */
    @GetMapping("/knowledge/{knowledgeId}")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataByKnowledgeId(
            @PathVariable @NotBlank String knowledgeId,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 根据知识点ID {} 获取文本题数据", userId, knowledgeId);

            TextQuestionDataResponse response = textQuestionDataService.getTextQuestionDataByKnowledgeId(knowledgeId, userId);
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", response));
                
        } catch (IllegalArgumentException e) {
            logger.warn("获取文本题数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("获取失败", e.getMessage()));
        } catch (Exception e) {
            logger.error("根据知识点ID获取文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 获取用户的所有文本题数据
     * GET /api/textquestions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTextQuestionData(
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 获取所有文本题数据", userId);

            List<TextQuestionDataResponse> responses = textQuestionDataService.getAllTextQuestionDataByUser(userId);
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", responses));
                
        } catch (Exception e) {
            logger.error("获取所有文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 根据文本题类型获取数据
     * GET /api/textquestions/type/{textType}
     */
    @GetMapping("/type/{textType}")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataByType(
            @PathVariable @Pattern(regexp = "^(fill|essay)$", message = "文本题类型必须是fill或essay") String textType,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 根据类型 {} 获取文本题数据", userId, textType);

            List<TextQuestionDataResponse> responses = textQuestionDataService.getTextQuestionDataByType(userId, textType);
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", responses));
                
        } catch (Exception e) {
            logger.error("根据类型获取文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 根据分类获取文本题数据
     * GET /api/textquestions/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataByCategory(
            @PathVariable @NotBlank String categoryId,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 根据分类 {} 获取文本题数据", userId, categoryId);

            List<TextQuestionDataResponse> responses = textQuestionDataService.getTextQuestionDataByCategory(userId, categoryId);
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", responses));
                
        } catch (Exception e) {
            logger.error("根据分类获取文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 分页获取文本题数据
     * GET /api/textquestions/page?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataWithPagination(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 分页获取文本题数据，页码: {}, 大小: {}", userId, page, size);

            // 创建排序对象
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            Page<TextQuestionDataResponse> responsePage = textQuestionDataService.getTextQuestionDataWithPagination(userId, pageable);
            
            // 构建分页信息
            Map<String, Object> result = new HashMap<>();
            result.put("content", responsePage.getContent());
            result.put("pagination", createPaginationInfo(responsePage));
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", result));
                
        } catch (Exception e) {
            logger.error("分页获取文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 复合条件查询文本题数据
     * GET /api/textquestions/search?categoryId=xxx&textType=fill&validationMode=exact&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTextQuestionDataByComplexConditions(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) @Pattern(regexp = "^(fill|essay)$", message = "文本题类型必须是fill或essay") String textType,
            @RequestParam(required = false) @Pattern(regexp = "^(exact|contains|regex|manual)$", message = "验证模式必须是exact、contains、regex或manual") String validationMode,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 复合条件查询文本题数据", userId);

            // 创建排序对象
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            Page<TextQuestionDataResponse> responsePage = textQuestionDataService.getTextQuestionDataByComplexConditions(
                userId, categoryId, textType, validationMode, pageable);
            
            // 构建分页信息
            Map<String, Object> result = new HashMap<>();
            result.put("content", responsePage.getContent());
            result.put("pagination", createPaginationInfo(responsePage));
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", result));
                
        } catch (Exception e) {
            logger.error("复合条件查询文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "查询文本题数据失败"));
        }
    }

    /**
     * 关键词搜索文本题数据
     * GET /api/textquestions/search/{keyword}
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchTextQuestionDataByKeyword(
            @PathVariable @NotBlank String keyword,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 关键词搜索文本题数据: {}", userId, keyword);

            List<TextQuestionDataResponse> responses = textQuestionDataService.searchTextQuestionData(userId, keyword);
            
            return ResponseEntity.ok(createSuccessResponse("搜索成功", responses));
                
        } catch (Exception e) {
            logger.error("关键词搜索文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "搜索文本题数据失败"));
        }
    }

    /**
     * 获取文本题数据统计信息
     * GET /api/textquestions/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataStatistics(
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            logger.info("用户 {} 获取文本题数据统计信息", userId);

            Map<String, Object> statistics = textQuestionDataService.getTextQuestionDataStatistics(userId);
            
            return ResponseEntity.ok(createSuccessResponse("获取统计信息成功", statistics));
                
        } catch (Exception e) {
            logger.error("获取文本题数据统计信息时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取统计信息失败"));
        }
    }

    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 创建分页信息
     */
    private Map<String, Object> createPaginationInfo(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("currentPage", page.getNumber());
        pagination.put("size", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        pagination.put("first", page.isFirst());
        pagination.put("last", page.isLast());
        return pagination;
    }
} 