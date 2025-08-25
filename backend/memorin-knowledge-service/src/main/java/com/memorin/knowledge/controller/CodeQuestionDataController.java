package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateCodeQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateCodeQuestionDataRequest;
import com.memorin.knowledge.dto.response.CodeQuestionDataResponse;
import com.memorin.knowledge.service.CodeQuestionDataService;
import com.memorin.knowledge.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编程题数据控制器
 * 提供编程题数据的RESTful API接口
 * 集成JWT认证和权限控制
 */
@RestController
@RequestMapping("/api/codequestions")
@CrossOrigin(origins = "*")
public class CodeQuestionDataController {

    private static final Logger logger = LoggerFactory.getLogger(CodeQuestionDataController.class);

    @Autowired
    private CodeQuestionDataService codeQuestionDataService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 从HttpServletRequest中提取用户ID
     */
    private String getUserIdFromRequest(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
        if (token == null) {
            throw new RuntimeException("未找到有效的JWT令牌");
        }
        String userId = jwtTokenUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无法从令牌中获取用户ID");
        }
        return userId;
    }

    // =========================== CRUD 操作 ===========================

    /**
     * 创建编程题数据
     */
    @PostMapping
    public ResponseEntity<?> createCodeQuestionData(@Valid @RequestBody CreateCodeQuestionDataRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            CodeQuestionDataResponse response = codeQuestionDataService.createCodeQuestionData(request, userId);
            
            logger.info("编程题数据创建成功，用户ID: {}, 编程题ID: {}", userId, response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("创建编程题数据失败", e);
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
    public ResponseEntity<?> getCodeQuestionDataById(@PathVariable String id,
                                                    HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            CodeQuestionDataResponse response = codeQuestionDataService.getCodeQuestionDataById(id, userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取编程题数据失败，ID: {}", id, e);
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
    public ResponseEntity<?> getCodeQuestionDataByKnowledgeId(@PathVariable String knowledgeId,
                                                             HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            CodeQuestionDataResponse response = codeQuestionDataService.getCodeQuestionDataByKnowledgeId(knowledgeId, userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("根据知识点ID获取编程题数据失败，知识点ID: {}", knowledgeId, e);
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
                                                   @Valid @RequestBody UpdateCodeQuestionDataRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            CodeQuestionDataResponse response = codeQuestionDataService.updateCodeQuestionData(id, request, userId);
            
            logger.info("编程题数据更新成功，用户ID: {}, 编程题ID: {}", userId, id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("更新编程题数据失败，ID: {}", id, e);
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
    public ResponseEntity<?> deleteCodeQuestionData(@PathVariable String id,
                                                   HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            codeQuestionDataService.deleteCodeQuestionData(id, userId);
            
            logger.info("编程题数据删除成功，用户ID: {}, 编程题ID: {}", userId, id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "编程题数据删除成功");
            response.put("deletedId", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("删除编程题数据失败，ID: {}", id, e);
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
    public ResponseEntity<?> getAllCodeQuestionData(HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getAllCodeQuestionDataByUser(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取用户所有编程题数据失败", e);
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
    public ResponseEntity<?> getCodeQuestionDataByLanguage(@PathVariable String language,
                                                          HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getCodeQuestionDataByLanguage(userId, language);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("language", language);
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("根据编程语言获取编程题数据失败，语言: {}", language, e);
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
    public ResponseEntity<?> getCodeQuestionDataByDifficulty(@PathVariable Integer level,
                                                            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getCodeQuestionDataByDifficulty(userId, level);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("difficultyLevel", level);
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("根据难度等级获取编程题数据失败，难度: {}", level, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据分类ID获取编程题数据
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getCodeQuestionDataByCategory(@PathVariable String categoryId,
                                                          HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getCodeQuestionDataByCategory(userId, categoryId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("categoryId", categoryId);
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("根据分类ID获取编程题数据失败，分类ID: {}", categoryId, e);
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
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CodeQuestionDataResponse> responsePage = codeQuestionDataService.getCodeQuestionDataWithPagination(userId, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responsePage.getContent());
            result.put("currentPage", responsePage.getNumber());
            result.put("totalPages", responsePage.getTotalPages());
            result.put("totalElements", responsePage.getTotalElements());
            result.put("size", responsePage.getSize());
            result.put("hasNext", responsePage.hasNext());
            result.put("hasPrevious", responsePage.hasPrevious());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("分页获取编程题数据失败", e);
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
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CodeQuestionDataResponse> responsePage = codeQuestionDataService.getCodeQuestionDataByComplexConditions(
                    userId, categoryId, programmingLanguage, difficultyLevel, minPoints, maxPoints, pageable);
            
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
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("复合条件查询编程题数据失败", e);
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
    public ResponseEntity<?> searchCodeQuestionDataByKeyword(@RequestParam String keyword,
                                                            HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.searchCodeQuestionData(userId, keyword);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("keyword", keyword);
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("关键词搜索编程题数据失败，关键词: {}", keyword, e);
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
    public ResponseEntity<?> searchCodeQuestionDataByCode(@RequestParam String keyword,
                                                         HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.searchCodeQuestionDataByCodeContent(userId, keyword);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("keyword", keyword);
            result.put("searchType", "code");
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("根据代码内容搜索编程题失败，关键词: {}", keyword, e);
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
    public ResponseEntity<?> searchCodeQuestionDataByHints(@RequestParam String keyword,
                                                          HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.searchCodeQuestionDataByHints(userId, keyword);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("keyword", keyword);
            result.put("searchType", "hints");
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("根据提示内容搜索编程题失败，关键词: {}", keyword, e);
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
    public ResponseEntity<?> getCodeQuestionDataStatistics(HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            Map<String, Object> statistics = codeQuestionDataService.getCodeQuestionDataStatistics(userId);
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("获取编程题数据统计信息失败", e);
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
    public ResponseEntity<?> getCodeQuestionDataWithMaxTestCases(HttpServletRequest httpRequest) {
        try {
            String userId = getUserIdFromRequest(httpRequest);
            List<CodeQuestionDataResponse> responses = codeQuestionDataService.getCodeQuestionDataWithMaxTestCases(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("codeQuestions", responses);
            result.put("total", responses.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取测试用例最多的编程题失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取编程题数据失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 