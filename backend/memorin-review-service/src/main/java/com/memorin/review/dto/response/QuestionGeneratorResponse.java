package com.memorin.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * 题目列表生成器响应DTO
 * 返回生成的题目列表、元数据和统计信息
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class QuestionGeneratorResponse {

    /**
     * 生成的题目列表
     */
    private List<QuestionItem> questions;

    /**
     * 生成统计信息
     */
    private GenerationStatistics statistics;

    /**
     * 生成元数据
     */
    private GenerationMetadata metadata;

    /**
     * 处理流程日志
     */
    private List<ProcessingStep> processingSteps;

    /**
     * 是否成功
     */
    private Boolean success = true;

    /**
     * 消息
     */
    private String message;

    /**
     * 生成时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // 构造函数
    public QuestionGeneratorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 题目项目类
     */
    public static class QuestionItem {
        /**
         * 知识点ID
         */
        private String knowledgePointId;

        /**
         * 题目类型 (text, choice, code)
         */
        private String questionType;

        /**
         * 题目标题
         */
        private String title;

        /**
         * 题目内容
         */
        private String content;

        /**
         * 选择题选项 (如果是选择题)
         */
        private List<String> choices;

        /**
         * 正确答案
         */
        private String correctAnswer;

        /**
         * 难度级别 (1-5)
         */
        private Integer difficulty;

        /**
         * 知识库ID
         */
        private String knowledgeBaseId;

        /**
         * 知识区域ID
         */
        private String knowledgeAreaId;

        /**
         * 标签列表
         */
        private List<String> tags;

        /**
         * 分类
         */
        private String category;

        /**
         * 复习相关信息
         */
        private ReviewInfo reviewInfo;

        /**
         * 生成元数据
         */
        private Map<String, Object> metadata;

        // 复习信息子类
        public static class ReviewInfo {
            private Double easeFactor;
            private Integer interval;
            private Integer reviewCount;
            private Integer correctCount;
            private LocalDateTime nextReviewTime;
            private LocalDateTime lastReviewTime;

            // Getter和Setter
            public Double getEaseFactor() { return easeFactor; }
            public void setEaseFactor(Double easeFactor) { this.easeFactor = easeFactor; }
            public Integer getInterval() { return interval; }
            public void setInterval(Integer interval) { this.interval = interval; }
            public Integer getReviewCount() { return reviewCount; }
            public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
            public Integer getCorrectCount() { return correctCount; }
            public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }
            public LocalDateTime getNextReviewTime() { return nextReviewTime; }
            public void setNextReviewTime(LocalDateTime nextReviewTime) { this.nextReviewTime = nextReviewTime; }
            public LocalDateTime getLastReviewTime() { return lastReviewTime; }
            public void setLastReviewTime(LocalDateTime lastReviewTime) { this.lastReviewTime = lastReviewTime; }
        }

        // Getter和Setter
        public String getKnowledgePointId() { return knowledgePointId; }
        public void setKnowledgePointId(String knowledgePointId) { this.knowledgePointId = knowledgePointId; }
        public String getQuestionType() { return questionType; }
        public void setQuestionType(String questionType) { this.questionType = questionType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<String> getChoices() { return choices; }
        public void setChoices(List<String> choices) { this.choices = choices; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getKnowledgeBaseId() { return knowledgeBaseId; }
        public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }
        public String getKnowledgeAreaId() { return knowledgeAreaId; }
        public void setKnowledgeAreaId(String knowledgeAreaId) { this.knowledgeAreaId = knowledgeAreaId; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public ReviewInfo getReviewInfo() { return reviewInfo; }
        public void setReviewInfo(ReviewInfo reviewInfo) { this.reviewInfo = reviewInfo; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 生成统计信息类
     */
    public static class GenerationStatistics {
        private Integer totalQuestionsGenerated;
        private Integer questionsBeforeFiltering;
        private Integer questionsAfterFiltering;
        private Integer questionsAfterSorting;
        private Integer questionsAfterLimiting;
        private Long processingTimeMs;
        private Map<String, Integer> questionTypeDistribution;
        private Map<String, Integer> difficultyDistribution;
        private Map<String, Integer> sourceDistribution;
        private Double averageDifficulty;
        private Double averageAccuracy;

        // Getter和Setter
        public Integer getTotalQuestionsGenerated() { return totalQuestionsGenerated; }
        public void setTotalQuestionsGenerated(Integer totalQuestionsGenerated) { this.totalQuestionsGenerated = totalQuestionsGenerated; }
        public Integer getQuestionsBeforeFiltering() { return questionsBeforeFiltering; }
        public void setQuestionsBeforeFiltering(Integer questionsBeforeFiltering) { this.questionsBeforeFiltering = questionsBeforeFiltering; }
        public Integer getQuestionsAfterFiltering() { return questionsAfterFiltering; }
        public void setQuestionsAfterFiltering(Integer questionsAfterFiltering) { this.questionsAfterFiltering = questionsAfterFiltering; }
        public Integer getQuestionsAfterSorting() { return questionsAfterSorting; }
        public void setQuestionsAfterSorting(Integer questionsAfterSorting) { this.questionsAfterSorting = questionsAfterSorting; }
        public Integer getQuestionsAfterLimiting() { return questionsAfterLimiting; }
        public void setQuestionsAfterLimiting(Integer questionsAfterLimiting) { this.questionsAfterLimiting = questionsAfterLimiting; }
        public Long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        public Map<String, Integer> getQuestionTypeDistribution() { return questionTypeDistribution; }
        public void setQuestionTypeDistribution(Map<String, Integer> questionTypeDistribution) { this.questionTypeDistribution = questionTypeDistribution; }
        public Map<String, Integer> getDifficultyDistribution() { return difficultyDistribution; }
        public void setDifficultyDistribution(Map<String, Integer> difficultyDistribution) { this.difficultyDistribution = difficultyDistribution; }
        public Map<String, Integer> getSourceDistribution() { return sourceDistribution; }
        public void setSourceDistribution(Map<String, Integer> sourceDistribution) { this.sourceDistribution = sourceDistribution; }
        public Double getAverageDifficulty() { return averageDifficulty; }
        public void setAverageDifficulty(Double averageDifficulty) { this.averageDifficulty = averageDifficulty; }
        public Double getAverageAccuracy() { return averageAccuracy; }
        public void setAverageAccuracy(Double averageAccuracy) { this.averageAccuracy = averageAccuracy; }
    }

    /**
     * 生成元数据类
     */
    public static class GenerationMetadata {
        private String sourceType;
        private Map<String, Object> sourceParams;
        private List<String> appliedFilters;
        private String sorterType;
        private String limiterType;
        private String generationId;
        private String userId;
        private LocalDateTime generatedAt;
        private String algorithmVersion;

        // Getter和Setter
        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }
        public Map<String, Object> getSourceParams() { return sourceParams; }
        public void setSourceParams(Map<String, Object> sourceParams) { this.sourceParams = sourceParams; }
        public List<String> getAppliedFilters() { return appliedFilters; }
        public void setAppliedFilters(List<String> appliedFilters) { this.appliedFilters = appliedFilters; }
        public String getSorterType() { return sorterType; }
        public void setSorterType(String sorterType) { this.sorterType = sorterType; }
        public String getLimiterType() { return limiterType; }
        public void setLimiterType(String limiterType) { this.limiterType = limiterType; }
        public String getGenerationId() { return generationId; }
        public void setGenerationId(String generationId) { this.generationId = generationId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        public String getAlgorithmVersion() { return algorithmVersion; }
        public void setAlgorithmVersion(String algorithmVersion) { this.algorithmVersion = algorithmVersion; }
    }

    /**
     * 处理步骤类
     */
    public static class ProcessingStep {
        private String stepName;
        private String stepType;
        private Integer inputCount;
        private Integer outputCount;
        private Long processingTimeMs;
        private String message;
        private Map<String, Object> details;

        public ProcessingStep(String stepName, String stepType) {
            this.stepName = stepName;
            this.stepType = stepType;
        }

        // Getter和Setter
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public String getStepType() { return stepType; }
        public void setStepType(String stepType) { this.stepType = stepType; }
        public Integer getInputCount() { return inputCount; }
        public void setInputCount(Integer inputCount) { this.inputCount = inputCount; }
        public Integer getOutputCount() { return outputCount; }
        public void setOutputCount(Integer outputCount) { this.outputCount = outputCount; }
        public Long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }

    // 主要字段的Getter和Setter
    public List<QuestionItem> getQuestions() { return questions; }
    public void setQuestions(List<QuestionItem> questions) { this.questions = questions; }

    public GenerationStatistics getStatistics() { return statistics; }
    public void setStatistics(GenerationStatistics statistics) { this.statistics = statistics; }

    public GenerationMetadata getMetadata() { return metadata; }
    public void setMetadata(GenerationMetadata metadata) { this.metadata = metadata; }

    public List<ProcessingStep> getProcessingSteps() { return processingSteps; }
    public void setProcessingSteps(List<ProcessingStep> processingSteps) { this.processingSteps = processingSteps; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "QuestionGeneratorResponse{" +
                "questionsCount=" + (questions != null ? questions.size() : 0) +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 