package com.memorin.review.controller;

import com.memorin.review.dto.request.QuestionGeneratorRequest;
import com.memorin.review.dto.response.QuestionGeneratorResponse;
import com.memorin.review.service.QuestionGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 题目列表生成器REST API控制器
 * 提供题目生成、预设模板、策略信息等接口
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/review/question-generator")
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QuestionGeneratorController {

    private static final Logger log = LoggerFactory.getLogger(QuestionGeneratorController.class);

    @Autowired
    private QuestionGeneratorService questionGeneratorService;

    /**
     * 生成题目列表
     * 核心API接口，支持完整的策略配置
     * 
     * @param request 题目生成配置
     * @return 生成结果
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateQuestionList(@Valid @RequestBody QuestionGeneratorRequest request) {
        try {
            log.info("[API] 开始生成题目列表，用户: {}, 数据源: {}", 
                    request.getUserId(), request.getSource().getType());
            
            QuestionGeneratorResponse response = questionGeneratorService.generateQuestionList(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("message", "题目列表生成成功");
            result.put("timestamp", LocalDateTime.now());
            
            log.info("[API] 题目列表生成完成，生成 {} 道题目", response.getQuestions().size());
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("[API] 题目生成参数错误: {}", e.getMessage());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "参数错误", e.getMessage());
            
        } catch (Exception e) {
            log.error("[API] 题目生成失败: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "生成失败", e.getMessage());
        }
    }

    /**
     * 知识库复习模板
     * 参照原系统的 QuestionListTemplates.knowledgeBaseReview()
     * 
     * @param baseId 知识库ID
     * @param onlyDue 是否只复习到期的
     * @param random 是否随机排序
     * @param limit 题目数量限制
     * @return 生成结果
     */
    @PostMapping("/templates/knowledge-base/{baseId}")
    public ResponseEntity<Map<String, Object>> generateKnowledgeBaseReview(
            @PathVariable String baseId,
            @RequestParam(defaultValue = "false") Boolean onlyDue,
            @RequestParam(defaultValue = "false") Boolean random,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String userId) {
        
        try {
            log.info("[API] 知识库复习模板，baseId: {}, onlyDue: {}, random: {}, limit: {}", 
                    baseId, onlyDue, random, limit);
            
            QuestionGeneratorRequest request = createKnowledgeBaseTemplate(baseId, onlyDue, random, limit, userId);
            QuestionGeneratorResponse response = questionGeneratorService.generateQuestionList(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("template", "知识库复习");
            result.put("message", "知识库复习题目生成成功");
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("[API] 知识库复习模板生成失败: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "模板生成失败", e.getMessage());
        }
    }

    /**
     * 智能复习模板
     * 参照原系统的 QuestionListTemplates.smartReview()
     * 
     * @param baseId 知识库ID (可选)
     * @param count 题目数量
     * @param onlyDue 是否只复习到期的
     * @param userId 用户ID
     * @return 生成结果
     */
    @PostMapping("/templates/smart-review")
    public ResponseEntity<Map<String, Object>> generateSmartReview(
            @RequestParam(required = false) String baseId,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "true") Boolean onlyDue,
            @RequestParam(required = false) String userId) {
        
        try {
            log.info("[API] 智能复习模板，baseId: {}, count: {}, onlyDue: {}", baseId, count, onlyDue);
            
            QuestionGeneratorRequest request = createSmartReviewTemplate(baseId, count, onlyDue, userId);
            QuestionGeneratorResponse response = questionGeneratorService.generateQuestionList(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("template", "智能复习");
            result.put("message", "智能复习题目生成成功");
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("[API] 智能复习模板生成失败: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "模板生成失败", e.getMessage());
        }
    }

    /**
     * 错题复习模板
     * 参照原系统的 QuestionListTemplates.mistakeReviewByBase()
     * 
     * @param baseId 知识库ID
     * @param random 是否随机排序
     * @param limit 题目数量限制
     * @param userId 用户ID
     * @return 生成结果
     */
    @PostMapping("/templates/mistake-review/{baseId}")
    public ResponseEntity<Map<String, Object>> generateMistakeReview(
            @PathVariable String baseId,
            @RequestParam(defaultValue = "false") Boolean random,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String userId) {
        
        try {
            log.info("[API] 错题复习模板，baseId: {}, random: {}, limit: {}", baseId, random, limit);
            
            QuestionGeneratorRequest request = createMistakeReviewTemplate(baseId, random, limit, userId);
            QuestionGeneratorResponse response = questionGeneratorService.generateQuestionList(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("template", "错题复习");
            result.put("message", "错题复习题目生成成功");
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("[API] 错题复习模板生成失败: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "模板生成失败", e.getMessage());
        }
    }

    /**
     * 弱项强化复习模板
     * 参照原系统的 QuestionListTemplates.weaknessReview()
     * 
     * @param baseId 知识库ID (可选)
     * @param count 题目数量
     * @param userId 用户ID
     * @return 生成结果
     */
    @PostMapping("/templates/weakness-review")
    public ResponseEntity<Map<String, Object>> generateWeaknessReview(
            @RequestParam(required = false) String baseId,
            @RequestParam(defaultValue = "15") Integer count,
            @RequestParam(required = false) String userId) {
        
        try {
            log.info("[API] 弱项强化复习模板，baseId: {}, count: {}", baseId, count);
            
            QuestionGeneratorRequest request = createWeaknessReviewTemplate(baseId, count, userId);
            QuestionGeneratorResponse response = questionGeneratorService.generateQuestionList(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("template", "弱项强化复习");
            result.put("message", "弱项强化复习题目生成成功");
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("[API] 弱项强化复习模板生成失败: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "模板生成失败", e.getMessage());
        }
    }

    /**
     * 获取可用的策略列表
     * 返回所有已注册的策略信息
     * 
     * @return 策略列表
     */
    @GetMapping("/strategies")
    public ResponseEntity<Map<String, Object>> getAvailableStrategies() {
        try {
            Map<String, Object> strategies = new HashMap<>();
            
            // 数据源策略
            List<Map<String, String>> dataSources = Arrays.asList(
                createStrategyInfo("all-knowledge", "全部知识点", "获取所有知识点作为题目"),
                createStrategyInfo("knowledge-base", "知识库", "从指定知识库获取题目"),
                createStrategyInfo("knowledge-area", "知识区域", "从指定知识区域获取题目"),
                createStrategyInfo("all-mistakes", "全部错题", "获取所有未解决的错题"),
                createStrategyInfo("mistakes-by-base", "知识库错题", "获取指定知识库的错题"),
                createStrategyInfo("mistakes-by-area", "知识区错题", "获取指定知识区的错题"),
                createStrategyInfo("custom-list", "自定义列表", "从指定的知识点列表获取题目")
            );
            
            // 过滤器策略
            List<Map<String, String>> filters = Arrays.asList(
                createStrategyInfo("due-for-review", "到期复习", "筛选到期需要复习的题目"),
                createStrategyInfo("by-difficulty", "按难度", "筛选指定难度范围的题目"),
                createStrategyInfo("by-accuracy", "按正确率", "筛选指定正确率范围的题目"),
                createStrategyInfo("by-tags", "按标签", "筛选包含指定标签的题目"),
                createStrategyInfo("by-category", "按分类", "筛选指定分类的题目"),
                createStrategyInfo("by-review-count", "按复习次数", "筛选指定复习次数范围的题目")
            );
            
            // 排序器策略
            List<Map<String, String>> sorters = Arrays.asList(
                createStrategyInfo("random", "随机排序", "随机打乱题目顺序"),
                createStrategyInfo("by-review-time", "按复习时间", "按下次复习时间排序"),
                createStrategyInfo("by-difficulty", "按难度", "按题目难度排序"),
                createStrategyInfo("by-accuracy", "按正确率", "按历史正确率排序"),
                createStrategyInfo("by-created-time", "按创建时间", "按题目创建时间排序"),
                createStrategyInfo("smart", "智能排序", "综合多个因素进行智能排序")
            );
            
            // 限制器策略
            List<Map<String, String>> limiters = Arrays.asList(
                createStrategyInfo("fixed-count", "固定数量", "限制为固定数量的题目"),
                createStrategyInfo("percentage", "百分比", "限制为总数的百分比"),
                createStrategyInfo("time-limit", "时间限制", "根据预估用时限制题目数量"),
                createStrategyInfo("smart-limit", "智能限制", "根据用户能力智能限制数量")
            );
            
            strategies.put("dataSources", dataSources);
            strategies.put("filters", filters);
            strategies.put("sorters", sorters);
            strategies.put("limiters", limiters);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", strategies);
            result.put("message", "策略列表获取成功");
            result.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("[API] 获取策略列表失败: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "获取失败", e.getMessage());
        }
    }

    /**
     * 测试接口
     * 用于验证服务是否正常工作
     * 
     * @return 测试结果
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", "题目生成器服务正常运行");
        result.put("message", "Question Generator Service is running");
        result.put("timestamp", LocalDateTime.now());
        result.put("version", "1.0.0");
        
        return ResponseEntity.ok(result);
    }

    // ======================== 私有辅助方法 ========================

    /**
     * 创建知识库复习模板
     */
    private QuestionGeneratorRequest createKnowledgeBaseTemplate(String baseId, Boolean onlyDue, 
                                                               Boolean random, Integer limit, String userId) {
        QuestionGeneratorRequest request = new QuestionGeneratorRequest();
        request.setUserId(userId);
        
        // 数据源配置
        QuestionGeneratorRequest.SourceConfig source = new QuestionGeneratorRequest.SourceConfig();
        source.setType("knowledge-base");
        Map<String, Object> sourceParams = new HashMap<>();
        sourceParams.put("baseId", baseId);
        source.setParams(sourceParams);
        request.setSource(source);
        
        // 过滤器配置
        if (onlyDue) {
            List<QuestionGeneratorRequest.FilterConfig> filters = new ArrayList<>();
            QuestionGeneratorRequest.FilterConfig dueFilter = new QuestionGeneratorRequest.FilterConfig();
            dueFilter.setType("due-for-review");
            filters.add(dueFilter);
            request.setFilters(filters);
        }
        
        // 排序器配置
        QuestionGeneratorRequest.SorterConfig sorter = new QuestionGeneratorRequest.SorterConfig();
        sorter.setType(random ? "random" : "smart");
        request.setSorter(sorter);
        
        // 限制器配置
        if (limit != null && limit > 0) {
            QuestionGeneratorRequest.LimiterConfig limiter = new QuestionGeneratorRequest.LimiterConfig();
            limiter.setType("fixed-count");
            Map<String, Object> limiterParams = new HashMap<>();
            limiterParams.put("count", limit);
            limiter.setParams(limiterParams);
            request.setLimiter(limiter);
        }
        
        return request;
    }

    /**
     * 创建智能复习模板
     */
    private QuestionGeneratorRequest createSmartReviewTemplate(String baseId, Integer count, 
                                                             Boolean onlyDue, String userId) {
        QuestionGeneratorRequest request = new QuestionGeneratorRequest();
        request.setUserId(userId);
        
        // 数据源配置
        QuestionGeneratorRequest.SourceConfig source = new QuestionGeneratorRequest.SourceConfig();
        if (baseId != null && !baseId.trim().isEmpty()) {
            source.setType("knowledge-base");
            Map<String, Object> sourceParams = new HashMap<>();
            sourceParams.put("baseId", baseId);
            source.setParams(sourceParams);
        } else {
            source.setType("all-knowledge");
        }
        request.setSource(source);
        
        // 过滤器配置
        if (onlyDue) {
            List<QuestionGeneratorRequest.FilterConfig> filters = new ArrayList<>();
            QuestionGeneratorRequest.FilterConfig dueFilter = new QuestionGeneratorRequest.FilterConfig();
            dueFilter.setType("due-for-review");
            filters.add(dueFilter);
            request.setFilters(filters);
        }
        
        // 智能排序
        QuestionGeneratorRequest.SorterConfig sorter = new QuestionGeneratorRequest.SorterConfig();
        sorter.setType("smart");
        request.setSorter(sorter);
        
        // 智能限制
        QuestionGeneratorRequest.LimiterConfig limiter = new QuestionGeneratorRequest.LimiterConfig();
        limiter.setType("smart-limit");
        Map<String, Object> limiterParams = new HashMap<>();
        limiterParams.put("baseCount", count);
        limiterParams.put("maxCount", count * 2);
        limiter.setParams(limiterParams);
        request.setLimiter(limiter);
        
        return request;
    }

    /**
     * 创建错题复习模板
     */
    private QuestionGeneratorRequest createMistakeReviewTemplate(String baseId, Boolean random, 
                                                               Integer limit, String userId) {
        QuestionGeneratorRequest request = new QuestionGeneratorRequest();
        request.setUserId(userId);
        
        // 数据源配置
        QuestionGeneratorRequest.SourceConfig source = new QuestionGeneratorRequest.SourceConfig();
        source.setType("mistakes-by-base");
        Map<String, Object> sourceParams = new HashMap<>();
        sourceParams.put("baseId", baseId);
        source.setParams(sourceParams);
        request.setSource(source);
        
        // 排序器配置
        QuestionGeneratorRequest.SorterConfig sorter = new QuestionGeneratorRequest.SorterConfig();
        sorter.setType(random ? "random" : "by-accuracy");
        if (!random) {
            Map<String, Object> sorterParams = new HashMap<>();
            sorterParams.put("order", "asc"); // 正确率从低到高
            sorter.setParams(sorterParams);
        }
        request.setSorter(sorter);
        
        // 限制器配置
        if (limit != null && limit > 0) {
            QuestionGeneratorRequest.LimiterConfig limiter = new QuestionGeneratorRequest.LimiterConfig();
            limiter.setType("fixed-count");
            Map<String, Object> limiterParams = new HashMap<>();
            limiterParams.put("count", limit);
            limiter.setParams(limiterParams);
            request.setLimiter(limiter);
        }
        
        return request;
    }

    /**
     * 创建弱项强化复习模板
     */
    private QuestionGeneratorRequest createWeaknessReviewTemplate(String baseId, Integer count, String userId) {
        QuestionGeneratorRequest request = new QuestionGeneratorRequest();
        request.setUserId(userId);
        
        // 数据源配置
        QuestionGeneratorRequest.SourceConfig source = new QuestionGeneratorRequest.SourceConfig();
        if (baseId != null && !baseId.trim().isEmpty()) {
            source.setType("knowledge-base");
            Map<String, Object> sourceParams = new HashMap<>();
            sourceParams.put("baseId", baseId);
            source.setParams(sourceParams);
        } else {
            source.setType("all-knowledge");
        }
        request.setSource(source);
        
        // 过滤器配置
        List<QuestionGeneratorRequest.FilterConfig> filters = new ArrayList<>();
        
        // 过滤低正确率题目
        QuestionGeneratorRequest.FilterConfig accuracyFilter = new QuestionGeneratorRequest.FilterConfig();
        accuracyFilter.setType("by-accuracy");
        Map<String, Object> accuracyParams = new HashMap<>();
        accuracyParams.put("maxAccuracy", 0.7); // 正确率低于70%
        accuracyFilter.setParams(accuracyParams);
        filters.add(accuracyFilter);
        
        // 过滤复习次数
        QuestionGeneratorRequest.FilterConfig countFilter = new QuestionGeneratorRequest.FilterConfig();
        countFilter.setType("by-review-count");
        Map<String, Object> countParams = new HashMap<>();
        countParams.put("minCount", 2); // 至少复习过2次
        countFilter.setParams(countParams);
        filters.add(countFilter);
        
        request.setFilters(filters);
        
        // 按正确率排序
        QuestionGeneratorRequest.SorterConfig sorter = new QuestionGeneratorRequest.SorterConfig();
        sorter.setType("by-accuracy");
        Map<String, Object> sorterParams = new HashMap<>();
        sorterParams.put("order", "asc"); // 正确率从低到高
        sorter.setParams(sorterParams);
        request.setSorter(sorter);
        
        // 固定数量限制
        QuestionGeneratorRequest.LimiterConfig limiter = new QuestionGeneratorRequest.LimiterConfig();
        limiter.setType("fixed-count");
        Map<String, Object> limiterParams = new HashMap<>();
        limiterParams.put("count", count);
        limiter.setParams(limiterParams);
        request.setLimiter(limiter);
        
        return request;
    }

    /**
     * 创建策略信息
     */
    private Map<String, String> createStrategyInfo(String name, String displayName, String description) {
        Map<String, String> info = new HashMap<>();
        info.put("name", name);
        info.put("displayName", displayName);
        info.put("description", description);
        return info;
    }

    /**
     * 构建错误响应
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", error);
        result.put("message", message);
        result.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(status).body(result);
    }
} 