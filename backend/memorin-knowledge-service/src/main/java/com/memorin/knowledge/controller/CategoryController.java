package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateCategoryRequest;
import com.memorin.knowledge.dto.request.UpdateCategoryRequest;
import com.memorin.knowledge.dto.response.CategoryResponse;
import com.memorin.knowledge.service.CategoryService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理API控制器
 * 提供分类的增删改查、树形结构、搜索等RESTful接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 创建新分类
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            CategoryResponse category = categoryService.createCategory(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类创建成功");
            response.put("data", category);
            
            logger.info("用户 {} 成功创建分类: {}", userId, category.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("创建分类失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("创建分类时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "创建分类失败");
        }
    }

    /**
     * 更新分类
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable @NotBlank(message = "分类ID不能为空") String id,
            @Valid @RequestBody UpdateCategoryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            CategoryResponse category = categoryService.updateCategory(id, request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类更新成功");
            response.put("data", category);
            
            logger.info("用户 {} 成功更新分类: {}", userId, category.getName());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("更新分类失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("更新分类时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "更新分类失败");
        }
    }

    /**
     * 删除分类
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(
            @PathVariable @NotBlank(message = "分类ID不能为空") String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            categoryService.deleteCategory(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类删除成功");
            
            logger.info("用户 {} 成功删除分类: {}", userId, id);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("删除分类失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("删除分类时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "删除分类失败");
        }
    }

    /**
     * 获取单个分类详情
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(
            @PathVariable @NotBlank(message = "分类ID不能为空") String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            CategoryResponse category = categoryService.getCategoryById(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取分类详情失败: {}", e.getMessage());
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("获取分类详情时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取分类详情失败");
        }
    }

    /**
     * 获取分类列表（扁平）
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            response.put("total", categories.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取分类列表时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取分类列表失败");
        }
    }

    /**
     * 获取分类树结构
     * GET /api/categories/tree
     */
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> getCategoryTree(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<CategoryResponse> categoryTree = categoryService.getCategoryTree(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categoryTree);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取分类树结构时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取分类树结构失败");
        }
    }

    /**
     * 分页获取分类列表
     * GET /api/categories/page
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getCategoriesWithPagination(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            Page<CategoryResponse> categoryPage = categoryService.getCategoriesWithPagination(userId, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categoryPage.getContent());
            response.put("pagination", createPaginationInfo(categoryPage));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("分页获取分类列表时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取分类列表失败");
        }
    }

    /**
     * 搜索分类
     * GET /api/categories/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCategories(
            @RequestParam @NotBlank(message = "搜索关键词不能为空") 
            @Size(min = 1, max = 100, message = "搜索关键词长度必须在1-100个字符之间") String keyword,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String userId = extractUserIdFromToken(authHeader);
        
        try {
            List<CategoryResponse> categories = categoryService.searchCategories(userId, keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            response.put("total", categories.size());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("搜索分类时发生错误", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "搜索分类失败");
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