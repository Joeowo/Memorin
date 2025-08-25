package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateCodeQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateCodeQuestionDataRequest;
import com.memorin.knowledge.dto.response.CodeQuestionDataResponse;
import com.memorin.knowledge.service.CodeQuestionDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编程题数据测试控制器
 * 用于开发调试，使用硬编码用户ID
 * ⚠️ 仅用于开发测试，生产环境禁用
 */
@RestController
@RequestMapping("/api/test/codequestions")
@CrossOrigin(origins = "*")
public class CodeQuestionDataTestController {

    private static final Logger logger = LoggerFactory.getLogger(CodeQuestionDataTestController.class);
    
    // 硬编码测试用户ID
    private static final String TEST_USER_ID = "USER_001";

    @Autowired
    private CodeQuestionDataService codeQuestionDataService;

    // =========================== CRUD 操作 ===========================

    /**
     * 创建编程题数据
     */
    @PostMapping
    public ResponseEntity<?> createCodeQuestionData(@Valid @RequestBody CreateCodeQuestionDataRequest request) {
        try {
            CodeQuestionDataResponse response = codeQuestionDataService.createCodeQuestionData(request, TEST_USER_ID);
            
            logger.info("测试环境编程题数据创建成功，编程题ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("测试环境创建编程题数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "创建编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据ID获取编程题数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCodeQuestionDataById(@PathVariable String id) {
        try {
            CodeQuestionDataResponse response = codeQuestionDataService.getCodeQuestionDataById(id, TEST_USER_ID);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("测试环境获取编程题数据失败，ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据知识点ID获取编程题数据
     */
    @GetMapping("/knowledge/{knowledgeId}")
    public ResponseEntity<?> getCodeQuestionDataByKnowledgeId(@PathVariable String knowledgeId) {
        try {
            CodeQuestionDataResponse response = codeQuestionDataService.getCodeQuestionDataByKnowledgeId(knowledgeId, TEST_USER_ID);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("测试环境根据知识点ID获取编程题数据失败，知识点ID: {}", knowledgeId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 更新编程题数据
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCodeQuestionData(@PathVariable String id,
                                                   @Valid @RequestBody UpdateCodeQuestionDataRequest request) {
        try {
            CodeQuestionDataResponse response = codeQuestionDataService.updateCodeQuestionData(id, request, TEST_USER_ID);
            
            logger.info("测试环境编程题数据更新成功，编程题ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("测试环境更新编程题数据失败，ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "更新编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 删除编程题数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCodeQuestionData(@PathVariable String id) {
        try {
            codeQuestionDataService.deleteCodeQuestionData(id, TEST_USER_ID);
            
            logger.info("测试环境编程题数据删除成功，编程题ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "编程题数据删除成功");
            response.put("deletedId", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("测试环境删除编程题数据失败，ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "删除编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // =========================== 查询操作 ===========================

    /**
     * 获取用户所有编程题数据
     */
    @GetMapping
    public ResponseEntity<?> getAllCodeQuestionData() {
        try {
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getAllCodeQuestionDataByUser(TEST_USER_ID);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("total", responses.size());
            result.put("testUserId", TEST_USER_ID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境获取用户所有编程题数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据编程语言获取编程题数据
     */
    @GetMapping("/language/{language}")
    public ResponseEntity<?> getCodeQuestionDataByLanguage(@PathVariable String language) {
        try {
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getCodeQuestionDataByLanguage(TEST_USER_ID, language);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("language", language);
            result.put("total", responses.size());
            result.put("testUserId", TEST_USER_ID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境根据编程语言获取编程题数据失败，语言: {}", language, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据难度等级获取编程题数据
     */
    @GetMapping("/difficulty/{level}")
    public ResponseEntity<?> getCodeQuestionDataByDifficulty(@PathVariable Integer level) {
        try {
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getCodeQuestionDataByDifficulty(TEST_USER_ID, level);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("difficultyLevel", level);
            result.put("total", responses.size());
            result.put("testUserId", TEST_USER_ID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境根据难度等级获取编程题数据失败，难度: {}", level, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 分页获取编程题数据
     */
    @GetMapping("/page")
    public ResponseEntity<?> getCodeQuestionDataWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CodeQuestionDataResponse> responsePage = codeQuestionDataService.getCodeQuestionDataWithPagination(TEST_USER_ID, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responsePage.getContent());
            result.put("currentPage", responsePage.getNumber());
            result.put("totalPages", responsePage.getTotalPages());
            result.put("totalElements", responsePage.getTotalElements());
            result.put("size", responsePage.getSize());
            result.put("hasNext", responsePage.hasNext());
            result.put("hasPrevious", responsePage.hasPrevious());
            result.put("testUserId", TEST_USER_ID);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境分页获取编程题数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 复合条件查询编程题数据
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCodeQuestionData(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String programmingLanguage,
            @RequestParam(required = false) Integer difficultyLevel,
            @RequestParam(required = false) Integer minPoints,
            @RequestParam(required = false) Integer maxPoints,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CodeQuestionDataResponse> responsePage = codeQuestionDataService.getCodeQuestionDataByComplexConditions(
                    TEST_USER_ID, categoryId, programmingLanguage, difficultyLevel, minPoints, maxPoints, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responsePage.getContent());
            
            // 构建搜索条件Map（处理null值）
            Map<String, Object> searchConditions = new HashMap<>();
            searchConditions.put("categoryId", categoryId);
            searchConditions.put("programmingLanguage", programmingLanguage);
            searchConditions.put("difficultyLevel", difficultyLevel);
            searchConditions.put("minPoints", minPoints);
            searchConditions.put("maxPoints", maxPoints);
            result.put("searchConditions", searchConditions);
            
            result.put("currentPage", responsePage.getNumber());
            result.put("totalPages", responsePage.getTotalPages());
            result.put("totalElements", responsePage.getTotalElements());
            result.put("size", responsePage.getSize());
            result.put("testUserId", TEST_USER_ID);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境复合条件查询编程题数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "查询编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // =========================== 搜索功能 ===========================

    /**
     * 关键词搜索编程题数据
     */
    @GetMapping("/search/keyword")
    public ResponseEntity<?> searchCodeQuestionDataByKeyword(@RequestParam String keyword) {
        try {
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.searchCodeQuestionData(TEST_USER_ID, keyword);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("keyword", keyword);
            result.put("total", responses.size());
            result.put("testUserId", TEST_USER_ID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境关键词搜索编程题数据失败，关键词: {}", keyword, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "搜索编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据代码内容搜索编程题
     */
    @GetMapping("/search/code")
    public ResponseEntity<?> searchCodeQuestionDataByCode(@RequestParam String keyword) {
        try {
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.searchCodeQuestionDataByCodeContent(TEST_USER_ID, keyword);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("keyword", keyword);
            result.put("searchType", "code");
            result.put("total", responses.size());
            result.put("testUserId", TEST_USER_ID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境根据代码内容搜索编程题失败，关键词: {}", keyword, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "搜索编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据提示内容搜索编程题
     */
    @GetMapping("/search/hints")
    public ResponseEntity<?> searchCodeQuestionDataByHints(@RequestParam String keyword) {
        try {
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.searchCodeQuestionDataByHints(TEST_USER_ID, keyword);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("keyword", keyword);
            result.put("searchType", "hints");
            result.put("total", responses.size());
            result.put("testUserId", TEST_USER_ID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境根据提示内容搜索编程题失败，关键词: {}", keyword, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "搜索编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // =========================== 统计分析 ===========================

    /**
     * 获取编程题数据统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getCodeQuestionDataStatistics() {
        try {
            Map<String, Object> statistics = codeQuestionDataService.getCodeQuestionDataStatistics(TEST_USER_ID);
            statistics.put("testUserId", TEST_USER_ID);
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("测试环境获取编程题数据统计信息失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取统计信息失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取测试用例数量最多的编程题
     */
    @GetMapping("/max-testcases")
    public ResponseEntity<?> getCodeQuestionDataWithMaxTestCases() {
        try {
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getCodeQuestionDataWithMaxTestCases(TEST_USER_ID);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("total", responses.size());
            result.put("testUserId", TEST_USER_ID);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("测试环境获取测试用例最多的编程题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // =========================== 测试辅助方法 ===========================

    /**
     * 获取当前测试用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<?> getTestInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("testUserId", TEST_USER_ID);
        info.put("description", "编程题数据测试控制器");
        info.put("warning", "⚠️ 仅用于开发测试，生产环境禁用");
        
        // 构建端点信息Map
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("create", "POST /api/test/codequestions");
        endpoints.put("getAll", "GET /api/test/codequestions");
        endpoints.put("getById", "GET /api/test/codequestions/{id}");
        endpoints.put("update", "PUT /api/test/codequestions/{id}");
        endpoints.put("delete", "DELETE /api/test/codequestions/{id}");
        endpoints.put("statistics", "GET /api/test/codequestions/statistics");
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }
} 