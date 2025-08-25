package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateChoiceQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateChoiceQuestionDataRequest;
import com.memorin.knowledge.dto.response.ChoiceQuestionDataResponse;
import com.memorin.knowledge.service.ChoiceQuestionDataService;
import com.memorin.knowledge.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * 选择题数据API控制器
 * 提供选择题数据的完整REST API接口
 */
@RestController
@RequestMapping("/api/choicequestions")
@Validated
public class ChoiceQuestionDataController {
    
    @Autowired
    private ChoiceQuestionDataService choiceQuestionDataService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    /**
     * 从请求中获取用户ID（JWT认证）
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenUtil.extractTokenFromHeader(request.getHeader("Authorization"));
        if (token != null && jwtTokenUtil.validateToken(token)) {
            return jwtTokenUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("无效的认证令牌");
    }
    
    // =========================== CRUD API ===========================
    
    /**
     * 创建选择题数据
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createChoiceQuestionData(
            @Valid @RequestBody CreateChoiceQuestionDataRequest request,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            ChoiceQuestionDataResponse response = choiceQuestionDataService.createChoiceQuestionData(request, userId);
            return ResponseEntity.ok(createSuccessResponse(response, "选择题数据创建成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 更新选择题数据
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateChoiceQuestionData(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody UpdateChoiceQuestionDataRequest request,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            ChoiceQuestionDataResponse response = choiceQuestionDataService.updateChoiceQuestionData(id, request, userId);
            return ResponseEntity.ok(createSuccessResponse(response, "选择题数据更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 删除选择题数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteChoiceQuestionData(
            @PathVariable @NotBlank String id,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            choiceQuestionDataService.deleteChoiceQuestionData(id, userId);
            return ResponseEntity.ok(createSuccessResponse(null, "选择题数据删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // =========================== 查询API ===========================
    
    /**
     * 根据ID获取选择题数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataById(
            @PathVariable @NotBlank String id,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            ChoiceQuestionDataResponse response = choiceQuestionDataService.getChoiceQuestionDataById(id, userId);
            return ResponseEntity.ok(createSuccessResponse(response, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据知识点ID获取选择题数据
     */
    @GetMapping("/knowledge/{knowledgeId}")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataByKnowledgeId(
            @PathVariable @NotBlank String knowledgeId,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            ChoiceQuestionDataResponse response = choiceQuestionDataService.getChoiceQuestionDataByKnowledgeId(knowledgeId, userId);
            return ResponseEntity.ok(createSuccessResponse(response, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取用户所有选择题数据
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllChoiceQuestionData(HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.getAllChoiceQuestionDataByUser(userId);
            return ResponseEntity.ok(createSuccessResponse(responses, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据选择题类型获取数据
     */
    @GetMapping("/type/{choiceType}")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataByType(
            @PathVariable @Pattern(regexp = "^(single|multiple)$", message = "选择题类型必须是single或multiple") String choiceType,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.getChoiceQuestionDataByType(userId, choiceType);
            return ResponseEntity.ok(createSuccessResponse(responses, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据分类ID获取选择题数据
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataByCategory(
            @PathVariable @NotBlank String categoryId,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.getChoiceQuestionDataByCategory(userId, categoryId);
            return ResponseEntity.ok(createSuccessResponse(responses, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 分页获取选择题数据
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataWithPagination(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            
            // 构建排序对象
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<ChoiceQuestionDataResponse> responsePage = choiceQuestionDataService.getChoiceQuestionDataWithPagination(userId, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", responsePage.getContent());
            result.put("pagination", createPaginationInfo(responsePage));
            
            return ResponseEntity.ok(createSuccessResponse(result, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据复合条件搜索选择题数据（分页）
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchChoiceQuestionDataByComplexConditions(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) @Pattern(regexp = "^(single|multiple)$", message = "选择题类型必须是single或multiple") String choiceType,
            @RequestParam(required = false) Integer minPoints,
            @RequestParam(required = false) Integer maxPoints,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            
            // 构建排序对象
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<ChoiceQuestionDataResponse> responsePage = choiceQuestionDataService.getChoiceQuestionDataByComplexConditions(
                    userId, categoryId, choiceType, minPoints, maxPoints, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", responsePage.getContent());
            result.put("pagination", createPaginationInfo(responsePage));
            
            return ResponseEntity.ok(createSuccessResponse(result, "搜索成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据关键词搜索选择题数据
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchChoiceQuestionDataByKeyword(
            @PathVariable @NotBlank String keyword,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.searchChoiceQuestionData(userId, keyword);
            return ResponseEntity.ok(createSuccessResponse(responses, "搜索成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据选项内容搜索选择题数据
     */
    @GetMapping("/search/option/{keyword}")
    public ResponseEntity<Map<String, Object>> searchChoiceQuestionDataByOptionContent(
            @PathVariable @NotBlank String keyword,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.searchChoiceQuestionDataByOptionContent(userId, keyword);
            return ResponseEntity.ok(createSuccessResponse(responses, "搜索成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取选择题数据统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataStatistics(HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            Map<String, Object> statistics = choiceQuestionDataService.getChoiceQuestionDataStatistics(userId);
            return ResponseEntity.ok(createSuccessResponse(statistics, "获取统计信息成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // =========================== 辅助方法 ===========================
    
    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(Object data, String message) {
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
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("data", null);
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
        pagination.put("pageSize", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        pagination.put("isFirst", page.isFirst());
        pagination.put("isLast", page.isLast());
        return pagination;
    }
} 