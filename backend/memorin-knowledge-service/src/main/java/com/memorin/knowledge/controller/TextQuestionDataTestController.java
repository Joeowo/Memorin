package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.CreateTextQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateTextQuestionDataRequest;
import com.memorin.knowledge.dto.request.ScoringPointRequest;
import com.memorin.knowledge.dto.response.TextQuestionDataResponse;
import com.memorin.knowledge.service.TextQuestionDataService;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本题数据测试API控制器
 * 用于开发测试，无需JWT认证
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/test/textquestions")
@CrossOrigin(origins = "*")
@Validated
public class TextQuestionDataTestController {

    private static final Logger logger = LoggerFactory.getLogger(TextQuestionDataTestController.class);
    
    // 测试用户ID
    private static final String TEST_USER_ID = "USER_001";

    @Autowired
    private TextQuestionDataService textQuestionDataService;

    /**
     * 创建演示文本题数据
     * POST /api/test/textquestions/demo
     */
    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> createDemoTextQuestionData() {
        try {
            logger.info("创建演示文本题数据");

            List<TextQuestionDataResponse> createdList = new ArrayList<>();

            // 创建填空题示例
            CreateTextQuestionDataRequest fillRequest = new CreateTextQuestionDataRequest();
            fillRequest.setKnowledgeId("KP_text_20250118_001"); // 需要已存在的text类型知识点
            fillRequest.setTextType("fill");
            fillRequest.setAnswer("Java");
            fillRequest.setValidationMode("exact");
            fillRequest.setAlternativeAnswers(Arrays.asList("java", "JAVA"));
            fillRequest.setCaseSensitive(false);

            try {
                TextQuestionDataResponse fillResponse = textQuestionDataService.createTextQuestionData(fillRequest, TEST_USER_ID);
                createdList.add(fillResponse);
                logger.info("创建填空题成功: {}", fillResponse.getId());
            } catch (Exception e) {
                logger.warn("创建填空题失败: {}", e.getMessage());
            }

            // 创建问答题示例
            CreateTextQuestionDataRequest essayRequest = new CreateTextQuestionDataRequest();
            essayRequest.setKnowledgeId("KP_text_20250118_002"); // 需要已存在的text类型知识点
            essayRequest.setTextType("essay");
            essayRequest.setAnswer("面向对象编程（OOP）是一种编程范式，它使用对象来设计应用程序和计算机程序。");
            essayRequest.setValidationMode("manual");
            essayRequest.setMinWordCount(50);
            essayRequest.setMaxWordCount(200);

            // 添加评分要点
            List<ScoringPointRequest> scoringPoints = new ArrayList<>();
            ScoringPointRequest point1 = new ScoringPointRequest();
            point1.setDescription("解释面向对象的概念");
            point1.setPoints(30);
            point1.setKeywords(Arrays.asList("面向对象", "对象", "类"));
            scoringPoints.add(point1);

            ScoringPointRequest point2 = new ScoringPointRequest();
            point2.setDescription("说明OOP的优势");
            point2.setPoints(25);
            point2.setKeywords(Arrays.asList("封装", "继承", "多态"));
            scoringPoints.add(point2);

            essayRequest.setScoringPoints(scoringPoints);

            try {
                TextQuestionDataResponse essayResponse = textQuestionDataService.createTextQuestionData(essayRequest, TEST_USER_ID);
                createdList.add(essayResponse);
                logger.info("创建问答题成功: {}", essayResponse.getId());
            } catch (Exception e) {
                logger.warn("创建问答题失败: {}", e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(createSuccessResponse("演示文本题数据创建成功", createdList));

        } catch (Exception e) {
            logger.error("创建演示文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "创建演示数据失败"));
        }
    }

    /**
     * 创建文本题数据
     * POST /api/test/textquestions
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTextQuestionData(
            @Valid @RequestBody CreateTextQuestionDataRequest request) {
        try {
            logger.info("测试创建文本题数据，知识点ID: {}", request.getKnowledgeId());

            TextQuestionDataResponse response = textQuestionDataService.createTextQuestionData(request, TEST_USER_ID);
            
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
     * PUT /api/test/textquestions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTextQuestionData(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody UpdateTextQuestionDataRequest request) {
        try {
            logger.info("测试更新文本题数据，ID: {}", id);

            TextQuestionDataResponse response = textQuestionDataService.updateTextQuestionData(id, request, TEST_USER_ID);
            
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
     * DELETE /api/test/textquestions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTextQuestionData(
            @PathVariable @NotBlank String id) {
        try {
            logger.info("测试删除文本题数据，ID: {}", id);

            textQuestionDataService.deleteTextQuestionData(id, TEST_USER_ID);
            
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
     * GET /api/test/textquestions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataById(
            @PathVariable @NotBlank String id) {
        try {
            logger.info("测试获取文本题数据详情，ID: {}", id);

            TextQuestionDataResponse response = textQuestionDataService.getTextQuestionDataById(id, TEST_USER_ID);
            
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
     * GET /api/test/textquestions/knowledge/{knowledgeId}
     */
    @GetMapping("/knowledge/{knowledgeId}")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataByKnowledgeId(
            @PathVariable @NotBlank String knowledgeId) {
        try {
            logger.info("测试根据知识点ID {} 获取文本题数据", knowledgeId);

            TextQuestionDataResponse response = textQuestionDataService.getTextQuestionDataByKnowledgeId(knowledgeId, TEST_USER_ID);
            
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
     * 获取所有文本题数据
     * GET /api/test/textquestions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTextQuestionData() {
        try {
            logger.info("测试获取所有文本题数据");

            List<TextQuestionDataResponse> responses = textQuestionDataService.getAllTextQuestionDataByUser(TEST_USER_ID);
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", responses));
                
        } catch (Exception e) {
            logger.error("获取所有文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 根据文本题类型获取数据
     * GET /api/test/textquestions/type/{textType}
     */
    @GetMapping("/type/{textType}")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataByType(
            @PathVariable @Pattern(regexp = "^(fill|essay)$", message = "文本题类型必须是fill或essay") String textType) {
        try {
            logger.info("测试根据类型 {} 获取文本题数据", textType);

            List<TextQuestionDataResponse> responses = textQuestionDataService.getTextQuestionDataByType(TEST_USER_ID, textType);
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", responses));
                
        } catch (Exception e) {
            logger.error("根据类型获取文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "获取文本题数据失败"));
        }
    }

    /**
     * 分页获取文本题数据
     * GET /api/test/textquestions/page?page=0&size=10
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataWithPagination(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            logger.info("测试分页获取文本题数据，页码: {}, 大小: {}", page, size);

            // 创建排序对象
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            Page<TextQuestionDataResponse> responsePage = textQuestionDataService.getTextQuestionDataWithPagination(TEST_USER_ID, pageable);
            
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
     * 关键词搜索文本题数据
     * GET /api/test/textquestions/search/{keyword}
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchTextQuestionDataByKeyword(
            @PathVariable @NotBlank String keyword) {
        try {
            logger.info("测试关键词搜索文本题数据: {}", keyword);

            List<TextQuestionDataResponse> responses = textQuestionDataService.searchTextQuestionData(TEST_USER_ID, keyword);
            
            return ResponseEntity.ok(createSuccessResponse("搜索成功", responses));
                
        } catch (Exception e) {
            logger.error("关键词搜索文本题数据时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("服务器内部错误", "搜索文本题数据失败"));
        }
    }

    /**
     * 获取文本题数据统计信息
     * GET /api/test/textquestions/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTextQuestionDataStatistics() {
        try {
            logger.info("测试获取文本题数据统计信息");

            Map<String, Object> statistics = textQuestionDataService.getTextQuestionDataStatistics(TEST_USER_ID);
            
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