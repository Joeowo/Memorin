package com.memorin.review.dto.request;

import javax.validation.constraints.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 题目列表生成器配置请求DTO
 * 完全参照原系统的question-list-generator.js的配置结构
 * 支持策略模式的数据源、过滤器、排序器、限制器配置
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class QuestionGeneratorRequest {

    /**
     * 数据源配置
     * 指定题目来源：knowledge-base, knowledge-area, all-mistakes, custom-list等
     */
    @Valid
    @NotNull(message = "数据源配置不能为空")
    private SourceConfig source;

    /**
     * 过滤器配置列表
     * 支持多个过滤条件：due-for-review, by-difficulty, by-accuracy等
     */
    @Valid
    private List<FilterConfig> filters;

    /**
     * 排序器配置
     * 支持排序策略：random, smart, by-review-time, by-difficulty等
     */
    @Valid
    private SorterConfig sorter;

    /**
     * 限制器配置
     * 支持数量限制：fixed-count, percentage, time-limit, smart-limit等
     */
    @Valid
    private LimiterConfig limiter;

    /**
     * 最终处理选项
     * 控制是否启用选择题打乱等后处理功能
     */
    @Valid
    private FinalProcessingOptions finalProcessing;

    /**
     * 用户ID (用于个性化生成)
     */
    @Size(max = 50, message = "用户ID长度不能超过50个字符")
    private String userId;

    /**
     * 生成标识 (可选，用于日志追踪)
     */
    @Size(max = 100, message = "生成标识长度不能超过100个字符")
    private String generationId;

    // 构造函数
    public QuestionGeneratorRequest() {}

    /**
     * 数据源配置类
     */
    public static class SourceConfig {
        /**
         * 数据源类型
         * 可选值：all-knowledge, knowledge-base, knowledge-area, 
         *        all-mistakes, mistakes-by-base, mistakes-by-area, custom-list
         */
        @NotBlank(message = "数据源类型不能为空")
        @Size(max = 50, message = "数据源类型长度不能超过50个字符")
        private String type;

        /**
         * 数据源参数
         * 如：baseId, areaId, knowledgeIds等
         */
        private Map<String, Object> params;

        // Getter和Setter
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    /**
     * 过滤器配置类
     */
    public static class FilterConfig {
        /**
         * 过滤器类型
         * 可选值：due-for-review, by-difficulty, by-accuracy, by-tags, by-category, by-review-count
         */
        @NotBlank(message = "过滤器类型不能为空")
        @Size(max = 50, message = "过滤器类型长度不能超过50个字符")
        private String type;

        /**
         * 过滤器参数
         * 如：minDifficulty, maxDifficulty, tags, categories等
         */
        private Map<String, Object> params;

        // Getter和Setter
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    /**
     * 排序器配置类
     */
    public static class SorterConfig {
        /**
         * 排序器类型
         * 可选值：random, by-review-time, by-difficulty, by-accuracy, by-created-time, smart
         */
        @NotBlank(message = "排序器类型不能为空")
        @Size(max = 50, message = "排序器类型长度不能超过50个字符")
        private String type;

        /**
         * 排序器参数
         * 如：order(asc/desc)等
         */
        private Map<String, Object> params;

        // Getter和Setter
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    /**
     * 限制器配置类
     */
    public static class LimiterConfig {
        /**
         * 限制器类型
         * 可选值：fixed-count, percentage, time-limit, smart-limit
         */
        @NotBlank(message = "限制器类型不能为空")
        @Size(max = 50, message = "限制器类型长度不能超过50个字符")
        private String type;

        /**
         * 限制器参数
         * 如：count, percentage, timeLimit, baseCount, maxCount等
         */
        private Map<String, Object> params;

        // Getter和Setter
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    /**
     * 最终处理选项类
     */
    public static class FinalProcessingOptions {
        /**
         * 是否启用选择题打乱
         */
        private Boolean enableChoiceShuffle = true;

        /**
         * 是否添加元数据
         */
        private Boolean addMetadata = true;

        /**
         * 是否记录生成日志
         */
        private Boolean logGeneration = true;

        // Getter和Setter
        public Boolean getEnableChoiceShuffle() { return enableChoiceShuffle; }
        public void setEnableChoiceShuffle(Boolean enableChoiceShuffle) { this.enableChoiceShuffle = enableChoiceShuffle; }
        public Boolean getAddMetadata() { return addMetadata; }
        public void setAddMetadata(Boolean addMetadata) { this.addMetadata = addMetadata; }
        public Boolean getLogGeneration() { return logGeneration; }
        public void setLogGeneration(Boolean logGeneration) { this.logGeneration = logGeneration; }
    }

    // 主要字段的Getter和Setter
    public SourceConfig getSource() { return source; }
    public void setSource(SourceConfig source) { this.source = source; }

    public List<FilterConfig> getFilters() { return filters; }
    public void setFilters(List<FilterConfig> filters) { this.filters = filters; }

    public SorterConfig getSorter() { return sorter; }
    public void setSorter(SorterConfig sorter) { this.sorter = sorter; }

    public LimiterConfig getLimiter() { return limiter; }
    public void setLimiter(LimiterConfig limiter) { this.limiter = limiter; }

    public FinalProcessingOptions getFinalProcessing() { return finalProcessing; }
    public void setFinalProcessing(FinalProcessingOptions finalProcessing) { this.finalProcessing = finalProcessing; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getGenerationId() { return generationId; }
    public void setGenerationId(String generationId) { this.generationId = generationId; }

    @Override
    public String toString() {
        return "QuestionGeneratorRequest{" +
                "source=" + source +
                ", filtersCount=" + (filters != null ? filters.size() : 0) +
                ", sorter=" + sorter +
                ", limiter=" + limiter +
                ", userId='" + userId + '\'' +
                ", generationId='" + generationId + '\'' +
                '}';
    }
} 