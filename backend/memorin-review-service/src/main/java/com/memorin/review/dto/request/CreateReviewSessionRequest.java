package com.memorin.review.dto.request;

import javax.validation.constraints.*;
import java.util.List;

/**
 * 创建复习会话请求DTO
 * 用于接收创建复习会话的参数
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class CreateReviewSessionRequest {

    /**
     * 复习模式
     * 如：smart-review, knowledge-base, knowledge-area, mistakes-review
     */
    @NotBlank(message = "复习模式不能为空")
    @Size(max = 50, message = "复习模式长度不能超过50个字符")
    private String reviewMode;

    /**
     * 目标知识库ID (可选)
     * 当复习模式为knowledge-base时使用
     */
    @Size(max = 50, message = "知识库ID长度不能超过50个字符")
    private String knowledgeBaseId;

    /**
     * 目标知识领域ID (可选)
     * 当复习模式为knowledge-area时使用
     */
    @Size(max = 50, message = "知识领域ID长度不能超过50个字符")
    private String knowledgeAreaId;

    /**
     * 目标题目数量
     */
    @NotNull(message = "题目数量不能为空")
    @Min(value = 1, message = "题目数量不能小于1")
    @Max(value = 200, message = "题目数量不能超过200")
    private Integer targetQuestionCount;

    /**
     * 难度级别过滤 (可选)
     * 1-5级，多个难度用逗号分隔
     */
    private List<Integer> difficultyLevels;

    /**
     * 题目类型过滤 (可选)
     * text, choice, code
     */
    private List<String> questionTypes;

    /**
     * 标签过滤 (可选)
     */
    private List<String> tags;

    /**
     * 是否只复习到期的知识点
     */
    private Boolean onlyDueQuestions = false;

    /**
     * 排序方式
     * random:随机, smart:智能排序, by-difficulty:按难度, by-review-time:按复习时间
     */
    @NotBlank(message = "排序方式不能为空")
    @Pattern(regexp = "^(random|smart|by-difficulty|by-review-time)$", 
             message = "排序方式必须是random、smart、by-difficulty或by-review-time")
    private String sortOrder = "smart";

    /**
     * 最大复习时长(分钟)
     */
    @Min(value = 5, message = "最大复习时长不能小于5分钟")
    @Max(value = 480, message = "最大复习时长不能超过480分钟")
    private Integer maxDurationMinutes = 60;

    /**
     * 是否自动开始
     */
    private Boolean autoStart = false;

    /**
     * 会话备注
     */
    @Size(max = 500, message = "会话备注长度不能超过500个字符")
    private String notes;

    /**
     * 自定义知识点ID列表 (可选)
     * 用于custom-review模式
     */
    private List<String> customKnowledgePointIds;

    /**
     * 错误知识点复习天数限制 (可选)
     * 仅复习最近N天内出错的知识点
     */
    @Min(value = 1, message = "错误知识点复习天数不能小于1天")
    @Max(value = 365, message = "错误知识点复习天数不能超过365天")
    private Integer mistakeReviewDays;

    /**
     * 复习会话配置JSON (可选)
     * 存储额外的配置参数
     */
    @Size(max = 1000, message = "会话配置长度不能超过1000个字符")
    private String sessionConfig;

    // 构造函数
    public CreateReviewSessionRequest() {}

    public CreateReviewSessionRequest(String reviewMode, Integer targetQuestionCount, String sortOrder) {
        this.reviewMode = reviewMode;
        this.targetQuestionCount = targetQuestionCount;
        this.sortOrder = sortOrder;
    }

    // Getter和Setter方法
    public String getReviewMode() {
        return reviewMode;
    }

    public void setReviewMode(String reviewMode) {
        this.reviewMode = reviewMode;
    }

    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public String getKnowledgeAreaId() {
        return knowledgeAreaId;
    }

    public void setKnowledgeAreaId(String knowledgeAreaId) {
        this.knowledgeAreaId = knowledgeAreaId;
    }

    public Integer getTargetQuestionCount() {
        return targetQuestionCount;
    }

    public void setTargetQuestionCount(Integer targetQuestionCount) {
        this.targetQuestionCount = targetQuestionCount;
    }

    public List<Integer> getDifficultyLevels() {
        return difficultyLevels;
    }

    public void setDifficultyLevels(List<Integer> difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
    }

    public List<String> getQuestionTypes() {
        return questionTypes;
    }

    public void setQuestionTypes(List<String> questionTypes) {
        this.questionTypes = questionTypes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getOnlyDueQuestions() {
        return onlyDueQuestions;
    }

    public void setOnlyDueQuestions(Boolean onlyDueQuestions) {
        this.onlyDueQuestions = onlyDueQuestions;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getMaxDurationMinutes() {
        return maxDurationMinutes;
    }

    public void setMaxDurationMinutes(Integer maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }

    public Boolean getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(Boolean autoStart) {
        this.autoStart = autoStart;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getCustomKnowledgePointIds() {
        return customKnowledgePointIds;
    }

    public void setCustomKnowledgePointIds(List<String> customKnowledgePointIds) {
        this.customKnowledgePointIds = customKnowledgePointIds;
    }

    public Integer getMistakeReviewDays() {
        return mistakeReviewDays;
    }

    public void setMistakeReviewDays(Integer mistakeReviewDays) {
        this.mistakeReviewDays = mistakeReviewDays;
    }

    public String getSessionConfig() {
        return sessionConfig;
    }

    public void setSessionConfig(String sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    @Override
    public String toString() {
        return "CreateReviewSessionRequest{" +
                "reviewMode='" + reviewMode + '\'' +
                ", knowledgeBaseId='" + knowledgeBaseId + '\'' +
                ", knowledgeAreaId='" + knowledgeAreaId + '\'' +
                ", targetQuestionCount=" + targetQuestionCount +
                ", difficultyLevels=" + difficultyLevels +
                ", questionTypes=" + questionTypes +
                ", tags=" + tags +
                ", onlyDueQuestions=" + onlyDueQuestions +
                ", sortOrder='" + sortOrder + '\'' +
                ", maxDurationMinutes=" + maxDurationMinutes +
                ", autoStart=" + autoStart +
                '}';
    }
} 