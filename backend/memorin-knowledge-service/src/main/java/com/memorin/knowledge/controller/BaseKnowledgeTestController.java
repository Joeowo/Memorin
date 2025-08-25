package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateBaseKnowledgeRequest;
import com.memorin.knowledge.dto.request.UpdateBaseKnowledgeRequest;
import com.memorin.knowledge.dto.response.BaseKnowledgeResponse;
import com.memorin.knowledge.service.BaseKnowledgeService;
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
 * 基础知识点测试控制器 - 用于演示知识点功能（无需JWT认证）
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/test/knowledge")
@CrossOrigin(origins = "*")
public class BaseKnowledgeTestController {

    @Autowired
    private BaseKnowledgeService baseKnowledgeService;

    private static final String TEST_USER_ID = "USER_001"; // 测试用户ID

    /**
     * 创建测试知识点
     * POST /api/test/knowledge
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTestKnowledge(@Valid @RequestBody CreateBaseKnowledgeRequest request) {
        try {
            BaseKnowledgeResponse knowledge = baseKnowledgeService.createKnowledge(request, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试知识点创建成功");
            response.put("data", knowledge);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 获取所有测试知识点
     * GET /api/test/knowledge
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTestKnowledge() {
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getAllKnowledge(TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 获取单个知识点详情
     * GET /api/test/knowledge/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTestKnowledgeById(@PathVariable String id) {
        try {
            BaseKnowledgeResponse knowledge = baseKnowledgeService.getKnowledgeById(id, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledge);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 更新测试知识点
     * PUT /api/test/knowledge/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTestKnowledge(
            @PathVariable String id, 
            @Valid @RequestBody UpdateBaseKnowledgeRequest request) {
        try {
            BaseKnowledgeResponse knowledge = baseKnowledgeService.updateKnowledge(id, request, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试知识点更新成功");
            response.put("data", knowledge);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 删除测试知识点
     * DELETE /api/test/knowledge/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTestKnowledge(@PathVariable String id) {
        try {
            baseKnowledgeService.deleteKnowledge(id, TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "测试知识点删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 搜索测试知识点
     * GET /api/test/knowledge/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTestKnowledge(@RequestParam String keyword) {
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.searchKnowledge(TEST_USER_ID, keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 获取知识点统计信息
     * GET /api/test/knowledge/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTestKnowledgeStatistics() {
        try {
            Map<String, Object> statistics = baseKnowledgeService.getKnowledgeStatistics(TEST_USER_ID);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据分类获取知识点
     * GET /api/test/knowledge/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getTestKnowledgeByCategory(@PathVariable String categoryId) {
        try {
            List<BaseKnowledgeResponse> knowledgeList = baseKnowledgeService.getKnowledgeByCategory(TEST_USER_ID, categoryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knowledgeList);
            response.put("total", knowledgeList.size());
            response.put("categoryId", categoryId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 创建演示知识点数据
     * POST /api/test/knowledge/demo
     */
    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> createDemoKnowledge() {
        try {
            // 需要先获取一个分类ID用于测试
            // 这里假设已经有分类了，使用一个示例分类ID
            String defaultCategoryId = "CAT_1_001_1130"; // 假设这是数学分类的ID

            // 创建文本题知识点
            CreateBaseKnowledgeRequest textKnowledge = new CreateBaseKnowledgeRequest();
            textKnowledge.setQuestion("什么是函数的极限？");
            textKnowledge.setExplanation("函数的极限是当自变量趋近某个值时，函数值趋近的值。它是微积分的基础概念之一。");
            textKnowledge.setCategoryId(defaultCategoryId);
            textKnowledge.setType("text");
            textKnowledge.setTags(Arrays.asList("数学", "微积分", "极限"));
            textKnowledge.setDifficulty(3);
            textKnowledge.setEstimatedTime(15);
            textKnowledge.setStatus("published");
            
            BaseKnowledgeResponse textResponse = baseKnowledgeService.createKnowledge(textKnowledge, TEST_USER_ID);

            // 创建选择题知识点
            CreateBaseKnowledgeRequest choiceKnowledge = new CreateBaseKnowledgeRequest();
            choiceKnowledge.setQuestion("以下哪个不是编程语言？");
            choiceKnowledge.setExplanation("HTML是超文本标记语言，主要用于网页结构设计，不是编程语言。");
            choiceKnowledge.setCategoryId(defaultCategoryId);
            choiceKnowledge.setType("choice");
            choiceKnowledge.setTags(Arrays.asList("编程", "基础知识"));
            choiceKnowledge.setDifficulty(2);
            choiceKnowledge.setEstimatedTime(10);
            choiceKnowledge.setStatus("published");
            
            BaseKnowledgeResponse choiceResponse = baseKnowledgeService.createKnowledge(choiceKnowledge, TEST_USER_ID);

            // 创建编程题知识点
            CreateBaseKnowledgeRequest codeKnowledge = new CreateBaseKnowledgeRequest();
            codeKnowledge.setQuestion("编写一个函数计算数组中所有元素的和");
            codeKnowledge.setExplanation("使用循环遍历数组，累加所有元素的值。注意处理空数组的情况。");
            codeKnowledge.setCategoryId(defaultCategoryId);
            codeKnowledge.setType("code");
            codeKnowledge.setTags(Arrays.asList("编程", "算法", "数组"));
            codeKnowledge.setDifficulty(4);
            codeKnowledge.setEstimatedTime(30);
            codeKnowledge.setStatus("draft");
            
            BaseKnowledgeResponse codeResponse = baseKnowledgeService.createKnowledge(codeKnowledge, TEST_USER_ID);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "演示知识点创建成功");
            response.put("knowledge", Arrays.asList(textResponse, choiceResponse, codeResponse));
            
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