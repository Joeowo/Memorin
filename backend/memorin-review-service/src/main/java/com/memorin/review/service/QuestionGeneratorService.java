package com.memorin.review.service;

import com.memorin.review.dto.request.QuestionGeneratorRequest;
import com.memorin.review.dto.response.QuestionGeneratorResponse;
import com.memorin.review.dto.response.QuestionGeneratorResponse.*;
import com.memorin.review.strategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 题目列表生成器核心服务
 * 完全参照原系统question-list-generator.js的处理流程和策略模式
 * 实现验证配置→获取基础数据→过滤→排序→限制→最终处理的完整pipeline
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
public class QuestionGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(QuestionGeneratorService.class);

    // 策略注册表
    private final Map<String, DataSourceStrategy> dataSourceStrategies = new ConcurrentHashMap<>();
    private final Map<String, FilterStrategy> filterStrategies = new ConcurrentHashMap<>();
    private final Map<String, SorterStrategy> sorterStrategies = new ConcurrentHashMap<>();
    private final Map<String, LimiterStrategy> limiterStrategies = new ConcurrentHashMap<>();

    // 生成统计
    private final Map<String, Integer> generationStats = new ConcurrentHashMap<>();

    /**
     * 核心接口：生成题目列表
     * 完全参照原系统的 generateQuestionList(config) 方法
     * 
     * @param request 生成配置请求
     * @return 生成结果响应
     */
    public QuestionGeneratorResponse generateQuestionList(QuestionGeneratorRequest request) {
        long startTime = System.currentTimeMillis();
        String generationId = request.getGenerationId() != null ? 
            request.getGenerationId() : generateId();
        
        log.info("[题目生成] 开始生成，ID: {}, 用户: {}", generationId, request.getUserId());

        QuestionGeneratorResponse response = new QuestionGeneratorResponse();
        List<ProcessingStep> processingSteps = new ArrayList<>();
        
        try {
            // 1. 验证配置
            ProcessingStep validateStep = new ProcessingStep("验证配置", "VALIDATION");
            long stepStart = System.currentTimeMillis();
            validateConfig(request);
            validateStep.setProcessingTimeMs(System.currentTimeMillis() - stepStart);
            validateStep.setMessage("配置验证通过");
            processingSteps.add(validateStep);
            
            // 2. 获取基础数据源
            ProcessingStep sourceStep = new ProcessingStep("获取基础数据", "DATA_SOURCE");
            stepStart = System.currentTimeMillis();
            List<QuestionItem> questions = getBaseQuestions(request.getSource(), request.getUserId());
            sourceStep.setInputCount(0);
            sourceStep.setOutputCount(questions.size());
            sourceStep.setProcessingTimeMs(System.currentTimeMillis() - stepStart);
            sourceStep.setMessage("从" + request.getSource().getType() + "获取到" + questions.size() + "道题目");
            processingSteps.add(sourceStep);
            
            log.info("[题目生成] 基础数据获取完成: {} 道题目", questions.size());
            
            // 3. 应用过滤器
            if (request.getFilters() != null && !request.getFilters().isEmpty()) {
                questions = applyFilters(questions, request.getFilters(), request.getUserId(), processingSteps);
            }
            
            // 4. 应用排序器
            if (request.getSorter() != null) {
                questions = applySorter(questions, request.getSorter(), request.getUserId(), processingSteps);
            }
            
            // 5. 应用限制器
            if (request.getLimiter() != null) {
                questions = applyLimiter(questions, request.getLimiter(), request.getUserId(), processingSteps);
            }
            
            // 6. 最终处理
            questions = applyFinalProcessing(questions, request, processingSteps);
            
            // 7. 构建响应
            response.setQuestions(questions);
            response.setStatistics(buildStatistics(questions, processingSteps, startTime));
            response.setMetadata(buildMetadata(request, generationId));
            response.setProcessingSteps(processingSteps);
            response.setSuccess(true);
            response.setMessage("题目列表生成完成: " + questions.size() + " 道题目");
            
            log.info("[题目生成] 生成完成，ID: {}, 最终题目数: {}, 耗时: {}ms", 
                    generationId, questions.size(), System.currentTimeMillis() - startTime);
            
            // 更新统计
            updateGenerationStats(request.getSource().getType());
            
        } catch (Exception error) {
            log.error("[题目生成] 生成失败，ID: {}, 错误: {}", generationId, error.getMessage(), error);
            response.setSuccess(false);
            response.setMessage("题目列表生成失败: " + error.getMessage());
            response.setProcessingSteps(processingSteps);
            
            throw new RuntimeException("题目列表生成失败: " + error.getMessage(), error);
        }
        
        return response;
    }

    /**
     * 验证配置
     * 参照原系统的 validateConfig(config) 方法
     */
    private void validateConfig(QuestionGeneratorRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("配置对象不能为空");
        }
        
        if (request.getSource() == null || request.getSource().getType() == null || 
            request.getSource().getType().trim().isEmpty()) {
            throw new IllegalArgumentException("必须指定数据源类型");
        }
        
        // 验证数据源策略是否存在
        if (!dataSourceStrategies.containsKey(request.getSource().getType())) {
            throw new IllegalArgumentException("未找到数据源策略: " + request.getSource().getType());
        }
        
        // 验证数据源参数
        DataSourceStrategy sourceStrategy = dataSourceStrategies.get(request.getSource().getType());
        sourceStrategy.validateParams(request.getSource().getParams() != null ? 
                                    request.getSource().getParams() : new HashMap<>());
        
        // 验证过滤器
        if (request.getFilters() != null) {
            for (QuestionGeneratorRequest.FilterConfig filter : request.getFilters()) {
                if (!filterStrategies.containsKey(filter.getType())) {
                    throw new IllegalArgumentException("未找到过滤器策略: " + filter.getType());
                }
                FilterStrategy filterStrategy = filterStrategies.get(filter.getType());
                filterStrategy.validateParams(filter.getParams() != null ? 
                                            filter.getParams() : new HashMap<>());
            }
        }
        
        // 验证排序器
        if (request.getSorter() != null) {
            if (!sorterStrategies.containsKey(request.getSorter().getType())) {
                throw new IllegalArgumentException("未找到排序器策略: " + request.getSorter().getType());
            }
            SorterStrategy sorterStrategy = sorterStrategies.get(request.getSorter().getType());
            sorterStrategy.validateParams(request.getSorter().getParams() != null ? 
                                        request.getSorter().getParams() : new HashMap<>());
        }
        
        // 验证限制器
        if (request.getLimiter() != null) {
            if (!limiterStrategies.containsKey(request.getLimiter().getType())) {
                throw new IllegalArgumentException("未找到限制器策略: " + request.getLimiter().getType());
            }
            LimiterStrategy limiterStrategy = limiterStrategies.get(request.getLimiter().getType());
            limiterStrategy.validateParams(request.getLimiter().getParams() != null ? 
                                         request.getLimiter().getParams() : new HashMap<>());
        }
    }

    /**
     * 获取基础题目数据
     * 参照原系统的 getBaseQuestions(source) 方法
     */
    private List<QuestionItem> getBaseQuestions(QuestionGeneratorRequest.SourceConfig source, String userId) throws Exception {
        DataSourceStrategy strategy = dataSourceStrategies.get(source.getType());
        if (strategy == null) {
            throw new IllegalArgumentException("未找到数据源策略: " + source.getType());
        }
        
        Map<String, Object> params = source.getParams() != null ? source.getParams() : new HashMap<>();
        return strategy.getBaseQuestions(params, userId);
    }

    /**
     * 应用过滤器
     * 参照原系统的 applyFilters(questions, filterConfigs) 方法
     */
    private List<QuestionItem> applyFilters(List<QuestionItem> questions, 
                                          List<QuestionGeneratorRequest.FilterConfig> filterConfigs, 
                                          String userId, List<ProcessingStep> processingSteps) throws Exception {
        List<QuestionItem> result = new ArrayList<>(questions);
        
        for (QuestionGeneratorRequest.FilterConfig filterConfig : filterConfigs) {
            ProcessingStep filterStep = new ProcessingStep("过滤器: " + filterConfig.getType(), "FILTER");
            long stepStart = System.currentTimeMillis();
            int inputCount = result.size();
            
            FilterStrategy filter = filterStrategies.get(filterConfig.getType());
            if (filter == null) {
                log.warn("[题目生成] 未找到过滤器: {}", filterConfig.getType());
                continue;
            }
            
            Map<String, Object> params = filterConfig.getParams() != null ? 
                                      filterConfig.getParams() : new HashMap<>();
            result = filter.filterQuestions(result, params, userId);
            
            filterStep.setInputCount(inputCount);
            filterStep.setOutputCount(result.size());
            filterStep.setProcessingTimeMs(System.currentTimeMillis() - stepStart);
            filterStep.setMessage(String.format("过滤器 %s: %d → %d", 
                                               filterConfig.getType(), inputCount, result.size()));
            processingSteps.add(filterStep);
            
            log.debug("[题目生成] 应用过滤器 {}: {} → {}", filterConfig.getType(), inputCount, result.size());
        }
        
        return result;
    }

    /**
     * 应用排序器
     * 参照原系统的 applySorter(questions, sorterConfig) 方法
     */
    private List<QuestionItem> applySorter(List<QuestionItem> questions, 
                                         QuestionGeneratorRequest.SorterConfig sorterConfig, 
                                         String userId, List<ProcessingStep> processingSteps) throws Exception {
        ProcessingStep sorterStep = new ProcessingStep("排序器: " + sorterConfig.getType(), "SORTER");
        long stepStart = System.currentTimeMillis();
        
        SorterStrategy sorter = sorterStrategies.get(sorterConfig.getType());
        if (sorter == null) {
            log.warn("[题目生成] 未找到排序器: {}", sorterConfig.getType());
            return questions;
        }
        
        Map<String, Object> params = sorterConfig.getParams() != null ? 
                                   sorterConfig.getParams() : new HashMap<>();
        List<QuestionItem> result = sorter.sortQuestions(new ArrayList<>(questions), params, userId);
        
        sorterStep.setInputCount(questions.size());
        sorterStep.setOutputCount(result.size());
        sorterStep.setProcessingTimeMs(System.currentTimeMillis() - stepStart);
        sorterStep.setMessage("使用" + sorterConfig.getType() + "排序器排序");
        processingSteps.add(sorterStep);
        
        log.debug("[题目生成] 应用排序器: {}", sorterConfig.getType());
        
        return result;
    }

    /**
     * 应用限制器
     * 参照原系统的 applyLimiter(questions, limiterConfig) 方法
     */
    private List<QuestionItem> applyLimiter(List<QuestionItem> questions, 
                                          QuestionGeneratorRequest.LimiterConfig limiterConfig, 
                                          String userId, List<ProcessingStep> processingSteps) throws Exception {
        ProcessingStep limiterStep = new ProcessingStep("限制器: " + limiterConfig.getType(), "LIMITER");
        long stepStart = System.currentTimeMillis();
        int inputCount = questions.size();
        
        LimiterStrategy limiter = limiterStrategies.get(limiterConfig.getType());
        if (limiter == null) {
            log.warn("[题目生成] 未找到限制器: {}", limiterConfig.getType());
            return questions;
        }
        
        Map<String, Object> params = limiterConfig.getParams() != null ? 
                                   limiterConfig.getParams() : new HashMap<>();
        List<QuestionItem> result = limiter.limitQuestions(new ArrayList<>(questions), params, userId);
        
        limiterStep.setInputCount(inputCount);
        limiterStep.setOutputCount(result.size());
        limiterStep.setProcessingTimeMs(System.currentTimeMillis() - stepStart);
        limiterStep.setMessage(String.format("限制器 %s: %d → %d", 
                                            limiterConfig.getType(), inputCount, result.size()));
        processingSteps.add(limiterStep);
        
        log.debug("[题目生成] 应用限制器 {}: {} → {}", limiterConfig.getType(), inputCount, result.size());
        
        return result;
    }

    /**
     * 最终处理
     * 参照原系统的 applyFinalProcessing(questions, config) 方法
     */
    private List<QuestionItem> applyFinalProcessing(List<QuestionItem> questions, 
                                                  QuestionGeneratorRequest request, 
                                                  List<ProcessingStep> processingSteps) {
        ProcessingStep finalStep = new ProcessingStep("最终处理", "FINAL_PROCESSING");
        long stepStart = System.currentTimeMillis();
        
        // 1. 添加元数据
        List<QuestionItem> processedQuestions = questions.stream()
            .map(question -> {
                if (question.getMetadata() == null) {
                    question.setMetadata(new HashMap<>());
                }
                Map<String, Object> metadata = question.getMetadata();
                metadata.put("index", questions.indexOf(question));
                metadata.put("generatedAt", System.currentTimeMillis());
                metadata.put("source", request.getSource().getType());
                metadata.put("totalCount", questions.size());
                return question;
            })
            .collect(Collectors.toList());
        
        // 2. 应用选择题打乱处理（如果启用）
        QuestionGeneratorRequest.FinalProcessingOptions finalProcessing = request.getFinalProcessing();
        if (finalProcessing != null && finalProcessing.getEnableChoiceShuffle() != null && 
            finalProcessing.getEnableChoiceShuffle()) {
            log.debug("[题目生成] 开始应用选择题打乱处理...");
            
            // 统计选择题
            long choiceCount = processedQuestions.stream()
                .filter(q -> "choice".equals(q.getQuestionType()))
                .count();
            
            if (choiceCount > 0) {
                log.debug("[题目生成] 发现 {} 道选择题，应用打乱处理", choiceCount);
                // TODO: 实现选择题打乱逻辑
            }
        }
        
        finalStep.setInputCount(questions.size());
        finalStep.setOutputCount(processedQuestions.size());
        finalStep.setProcessingTimeMs(System.currentTimeMillis() - stepStart);
        finalStep.setMessage("添加元数据和后处理完成");
        processingSteps.add(finalStep);
        
        log.debug("[题目生成] 最终处理完成: {} 道题目", processedQuestions.size());
        
        return processedQuestions;
    }

    // ======================== 策略注册方法 ========================

    /**
     * 注册数据源策略
     */
    public void registerDataSourceStrategy(DataSourceStrategy strategy) {
        dataSourceStrategies.put(strategy.getStrategyName(), strategy);
        log.info("[策略注册] 数据源策略: {} - {}", strategy.getStrategyName(), strategy.getDescription());
    }

    /**
     * 注册过滤器策略
     */
    public void registerFilterStrategy(FilterStrategy strategy) {
        filterStrategies.put(strategy.getStrategyName(), strategy);
        log.info("[策略注册] 过滤器策略: {} - {}", strategy.getStrategyName(), strategy.getDescription());
    }

    /**
     * 注册排序器策略
     */
    public void registerSorterStrategy(SorterStrategy strategy) {
        sorterStrategies.put(strategy.getStrategyName(), strategy);
        log.info("[策略注册] 排序器策略: {} - {}", strategy.getStrategyName(), strategy.getDescription());
    }

    /**
     * 注册限制器策略
     */
    public void registerLimiterStrategy(LimiterStrategy strategy) {
        limiterStrategies.put(strategy.getStrategyName(), strategy);
        log.info("[策略注册] 限制器策略: {} - {}", strategy.getStrategyName(), strategy.getDescription());
    }

    // ======================== 辅助方法 ========================

    /**
     * 生成ID
     */
    private String generateId() {
        return "QG_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    /**
     * 构建统计信息
     */
    private GenerationStatistics buildStatistics(List<QuestionItem> questions, 
                                                List<ProcessingStep> steps, 
                                                long startTime) {
        GenerationStatistics stats = new GenerationStatistics();
        stats.setTotalQuestionsGenerated(questions.size());
        stats.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        
        // 统计题目类型分布
        Map<String, Integer> typeDistribution = questions.stream()
            .collect(Collectors.groupingBy(
                q -> q.getQuestionType() != null ? q.getQuestionType() : "unknown",
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        stats.setQuestionTypeDistribution(typeDistribution);
        
        // 统计难度分布
        Map<String, Integer> difficultyDistribution = questions.stream()
            .collect(Collectors.groupingBy(
                q -> "难度" + (q.getDifficulty() != null ? q.getDifficulty() : 0),
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        stats.setDifficultyDistribution(difficultyDistribution);
        
        // 计算平均难度
        double avgDifficulty = questions.stream()
            .filter(q -> q.getDifficulty() != null)
            .mapToInt(QuestionItem::getDifficulty)
            .average()
            .orElse(0.0);
        stats.setAverageDifficulty(avgDifficulty);
        
        // 设置处理步骤统计
        steps.stream()
            .filter(step -> "DATA_SOURCE".equals(step.getStepType()))
            .findFirst()
            .ifPresent(step -> stats.setQuestionsBeforeFiltering(step.getOutputCount()));
            
        return stats;
    }

    /**
     * 构建元数据
     */
    private GenerationMetadata buildMetadata(QuestionGeneratorRequest request, String generationId) {
        GenerationMetadata metadata = new GenerationMetadata();
        metadata.setSourceType(request.getSource().getType());
        metadata.setSourceParams(request.getSource().getParams());
        metadata.setGenerationId(generationId);
        metadata.setUserId(request.getUserId());
        metadata.setGeneratedAt(LocalDateTime.now());
        metadata.setAlgorithmVersion("1.0.0");
        
        if (request.getFilters() != null) {
            List<String> appliedFilters = request.getFilters().stream()
                .map(QuestionGeneratorRequest.FilterConfig::getType)
                .collect(Collectors.toList());
            metadata.setAppliedFilters(appliedFilters);
        }
        
        if (request.getSorter() != null) {
            metadata.setSorterType(request.getSorter().getType());
        }
        
        if (request.getLimiter() != null) {
            metadata.setLimiterType(request.getLimiter().getType());
        }
        
        return metadata;
    }

    /**
     * 更新生成统计
     */
    private void updateGenerationStats(String sourceType) {
        generationStats.merge(sourceType, 1, Integer::sum);
    }
} 