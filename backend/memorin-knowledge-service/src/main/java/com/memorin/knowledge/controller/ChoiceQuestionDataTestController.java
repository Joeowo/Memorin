package com.memorin.knowledge.controller;

import com.memorin.knowledge.dto.request.ChoiceOptionRequest;
import com.memorin.knowledge.dto.request.CreateChoiceQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateChoiceQuestionDataRequest;
import com.memorin.knowledge.dto.response.ChoiceQuestionDataResponse;
import com.memorin.knowledge.service.ChoiceQuestionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 选择题数据测试API控制器
 * 用于开发测试，无需JWT认证
 */
@RestController
@RequestMapping("/api/test/choicequestions")
@Validated
public class ChoiceQuestionDataTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChoiceQuestionDataTestController.class);
    
    // 测试用户ID，与其他服务保持一致
    private static final String TEST_USER_ID = "USER_001";
    
    @Autowired
    private ChoiceQuestionDataService choiceQuestionDataService;
    
    // =========================== 测试数据创建 ===========================
    
    /**
     * 创建演示选择题数据
     */
    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> createDemoChoiceQuestionData() {
        try {
            List<ChoiceQuestionDataResponse> createdData = new ArrayList<>();
            
            // 创建演示单选题
            CreateChoiceQuestionDataRequest singleChoiceRequest = createDemoSingleChoiceRequest();
            try {
                ChoiceQuestionDataResponse singleResponse = choiceQuestionDataService.createChoiceQuestionData(singleChoiceRequest, TEST_USER_ID);
                createdData.add(singleResponse);
            } catch (Exception e) {
                // 忽略已存在的错误
            }
            
            // 创建演示多选题
            CreateChoiceQuestionDataRequest multipleChoiceRequest = createDemoMultipleChoiceRequest();
            try {
                ChoiceQuestionDataResponse multipleResponse = choiceQuestionDataService.createChoiceQuestionData(multipleChoiceRequest, TEST_USER_ID);
                createdData.add(multipleResponse);
            } catch (Exception e) {
                // 忽略已存在的错误
            }
            
            return ResponseEntity.ok(createSuccessResponse(createdData, "演示数据创建成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // =========================== CRUD API ===========================
    
    /**
     * 创建选择题数据
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createChoiceQuestionData(@Valid @RequestBody CreateChoiceQuestionDataRequest request) {
        try {
            ChoiceQuestionDataResponse response = choiceQuestionDataService.createChoiceQuestionData(request, TEST_USER_ID);
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
            @Valid @RequestBody UpdateChoiceQuestionDataRequest request) {
        try {
            ChoiceQuestionDataResponse response = choiceQuestionDataService.updateChoiceQuestionData(id, request, TEST_USER_ID);
            return ResponseEntity.ok(createSuccessResponse(response, "选择题数据更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 删除选择题数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteChoiceQuestionData(@PathVariable @NotBlank String id) {
        try {
            choiceQuestionDataService.deleteChoiceQuestionData(id, TEST_USER_ID);
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
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataById(@PathVariable @NotBlank String id) {
        try {
            ChoiceQuestionDataResponse response = choiceQuestionDataService.getChoiceQuestionDataById(id, TEST_USER_ID);
            return ResponseEntity.ok(createSuccessResponse(response, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据知识点ID获取选择题数据
     */
    @GetMapping("/knowledge/{knowledgeId}")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataByKnowledgeId(@PathVariable @NotBlank String knowledgeId) {
        try {
            ChoiceQuestionDataResponse response = choiceQuestionDataService.getChoiceQuestionDataByKnowledgeId(knowledgeId, TEST_USER_ID);
            return ResponseEntity.ok(createSuccessResponse(response, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取所有选择题数据
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllChoiceQuestionData() {
        try {
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.getAllChoiceQuestionDataByUser(TEST_USER_ID);
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
            @PathVariable @Pattern(regexp = "^(single|multiple)$", message = "选择题类型必须是single或multiple") String choiceType) {
        try {
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.getChoiceQuestionDataByType(TEST_USER_ID, choiceType);
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
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            // 构建排序对象
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<ChoiceQuestionDataResponse> responsePage = choiceQuestionDataService.getChoiceQuestionDataWithPagination(TEST_USER_ID, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", responsePage.getContent());
            result.put("pagination", createPaginationInfo(responsePage));
            
            return ResponseEntity.ok(createSuccessResponse(result, "获取成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 根据关键词搜索选择题数据
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchChoiceQuestionDataByKeyword(@PathVariable @NotBlank String keyword) {
        try {
            List<ChoiceQuestionDataResponse> responses = choiceQuestionDataService.searchChoiceQuestionData(TEST_USER_ID, keyword);
            return ResponseEntity.ok(createSuccessResponse(responses, "搜索成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取选择题数据统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getChoiceQuestionDataStatistics() {
        try {
            Map<String, Object> statistics = choiceQuestionDataService.getChoiceQuestionDataStatistics(TEST_USER_ID);
            return ResponseEntity.ok(createSuccessResponse(statistics, "获取统计信息成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // =========================== 私有辅助方法 ===========================
    
    /**
     * 创建演示单选题请求
     */
    private CreateChoiceQuestionDataRequest createDemoSingleChoiceRequest() {
        CreateChoiceQuestionDataRequest request = new CreateChoiceQuestionDataRequest();
        request.setKnowledgeId("KP_text_20250118_001"); // 假设这是已存在的知识点ID
        request.setChoiceType("single");
        request.setPoints(10);
        request.setPartialCredit(false);
        request.setRandomOrder(false);
        request.setExplanation("这是一道关于Java编程语言的单选题");
        
        List<ChoiceOptionRequest> options = new ArrayList<>();
        options.add(new ChoiceOptionRequest("A", "面向对象的编程语言", true, "正确，Java是面向对象的编程语言", 0));
        options.add(new ChoiceOptionRequest("B", "函数式编程语言", false, "错误，Java主要是面向对象的", 1));
        options.add(new ChoiceOptionRequest("C", "汇编语言", false, "错误，Java是高级编程语言", 2));
        options.add(new ChoiceOptionRequest("D", "脚本语言", false, "错误，Java是编译型语言", 3));
        
        request.setOptions(options);
        return request;
    }
    
    /**
     * 创建演示多选题请求
     */
    private CreateChoiceQuestionDataRequest createDemoMultipleChoiceRequest() {
        CreateChoiceQuestionDataRequest request = new CreateChoiceQuestionDataRequest();
        request.setKnowledgeId("KP_text_20250118_002"); // 假设这是已存在的知识点ID
        request.setChoiceType("multiple");
        request.setPoints(20);
        request.setPartialCredit(true);
        request.setRandomOrder(true);
        request.setExplanation("这是一道关于Java特性的多选题，支持部分得分");
        
        List<ChoiceOptionRequest> options = new ArrayList<>();
        options.add(new ChoiceOptionRequest("A", "跨平台性", true, "正确，Java具有跨平台特性", 0));
        options.add(new ChoiceOptionRequest("B", "面向对象", true, "正确，Java是面向对象语言", 1));
        options.add(new ChoiceOptionRequest("C", "内存管理", true, "正确，Java有自动内存管理", 2));
        options.add(new ChoiceOptionRequest("D", "指针操作", false, "错误，Java不支持指针操作", 3));
        
        request.setOptions(options);
        return request;
    }
    
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