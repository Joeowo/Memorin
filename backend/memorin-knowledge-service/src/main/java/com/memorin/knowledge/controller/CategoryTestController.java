package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateCategoryRequest;
import com.memorin.knowledge.dto.request.UpdateCategoryRequest;
import com.memorin.knowledge.dto.response.CategoryResponse;
import com.memorin.knowledge.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理测试控制器 - 用于演示分类功能（无需JWT认证）
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/test/categories")
@CrossOrigin(origins = "*")
public class CategoryTestController {

    @Autowired
    private CategoryService categoryService;

    private static final String TEST_USER_ID = "USER_001"; // 测试用户ID

    /**
     * 创建测试分类
     * POST /api/test/categories
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTestCategory(@Valid @RequestBody CreateCategoryRequest request) {
        try {
            CategoryResponse category = categoryService.createCategory(request, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试分类创建成功");
            response.put("data", category);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 获取所有测试分类
     * GET /api/test/categories
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTestCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories(TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            response.put("total", categories.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 获取分类树结构
     * GET /api/test/categories/tree
     */
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> getTestCategoryTree() {
        try {
            List<CategoryResponse> categoryTree = categoryService.getCategoryTree(TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categoryTree);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 获取单个分类详情
     * GET /api/test/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTestCategoryById(@PathVariable String id) {
        try {
            CategoryResponse category = categoryService.getCategoryById(id, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 更新测试分类
     * PUT /api/test/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTestCategory(
            @PathVariable String id, 
            @Valid @RequestBody UpdateCategoryRequest request) {
        try {
            CategoryResponse category = categoryService.updateCategory(id, request, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试分类更新成功");
            response.put("data", category);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 删除测试分类
     * DELETE /api/test/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTestCategory(@PathVariable String id) {
        try {
            categoryService.deleteCategory(id, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试分类删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 搜索测试分类
     * GET /api/test/categories/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTestCategories(@RequestParam String keyword) {
        try {
            List<CategoryResponse> categories = categoryService.searchCategories(TEST_USER_ID, keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            response.put("total", categories.size());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 创建演示数据
     * POST /api/test/categories/demo
     */
    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> createDemoData() {
        try {
            // 创建顶级分类：数学
            CreateCategoryRequest mathCategory = new CreateCategoryRequest();
            mathCategory.setName("数学");
            mathCategory.setColor("#667eea");
            mathCategory.setIcon("📐");
            mathCategory.setDescription("数学相关知识");
            CategoryResponse math = categoryService.createCategory(mathCategory, TEST_USER_ID);

            // 创建子分类：高等数学
            CreateCategoryRequest calculus = new CreateCategoryRequest();
            calculus.setName("高等数学");
            calculus.setParentId(math.getId());
            calculus.setColor("#764ba2");
            calculus.setIcon("📊");
            calculus.setDescription("高等数学相关内容");
            CategoryResponse calculusResponse = categoryService.createCategory(calculus, TEST_USER_ID);

            // 创建子子分类：极限理论
            CreateCategoryRequest limits = new CreateCategoryRequest();
            limits.setName("极限理论");
            limits.setParentId(calculusResponse.getId());
            limits.setColor("#f093fb");
            limits.setIcon("∞");
            limits.setDescription("函数极限相关知识");
            CategoryResponse limitsResponse = categoryService.createCategory(limits, TEST_USER_ID);

            // 创建另一个顶级分类：编程
            CreateCategoryRequest programming = new CreateCategoryRequest();
            programming.setName("编程");
            programming.setColor("#43e97b");
            programming.setIcon("💻");
            programming.setDescription("编程语言和技术");
            CategoryResponse progResponse = categoryService.createCategory(programming, TEST_USER_ID);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "演示数据创建成功");
            response.put("categories", Arrays.asList(math, calculusResponse, limitsResponse, progResponse));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(status).body(response);
    }
} 