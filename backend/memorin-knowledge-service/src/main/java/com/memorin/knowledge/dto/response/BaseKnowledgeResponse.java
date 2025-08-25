package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.BaseKnowledge;

import java.util.List;

/**
 * 基础知识点响应DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class BaseKnowledgeResponse {

    /**
     * 知识点唯一标识符
     */
    private String id;

    /**
     * 问题内容
     */
    private String question;

    /**
     * 答案解析
     */
    private String explanation;

    /**
     * 所属分类ID
     */
    private String categoryId;

    /**
     * 题目类型
     */
    private String type;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 难度等级
     */
    private Integer difficulty;

    /**
     * 预估学习时间(分钟)
     */
    private Integer estimatedTime;

    /**
     * 知识点状态
     */
    private String status;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 关联用户ID
     */
    private String userId;

    /**
     * 创建时间戳
     */
    private Long createdAt;

    /**
     * 更新时间戳
     */
    private Long updatedAt;

    // 构造函数
    public BaseKnowledgeResponse() {}

    /**
     * 从BaseKnowledge实体转换为BaseKnowledgeResponse
     */
    public static BaseKnowledgeResponse fromEntity(BaseKnowledge knowledge) {
        BaseKnowledgeResponse response = new BaseKnowledgeResponse();
        response.setId(knowledge.getId());
        response.setQuestion(knowledge.getQuestion());
        response.setExplanation(knowledge.getExplanation());
        response.setCategoryId(knowledge.getCategoryId());
        response.setType(knowledge.getType());
        response.setTags(knowledge.getTagList()); // 使用实体的getTagList方法
        response.setDifficulty(knowledge.getDifficulty());
        response.setEstimatedTime(knowledge.getEstimatedTime());
        response.setStatus(knowledge.getStatus());
        response.setIsActive(knowledge.getIsActive());
        response.setUserId(knowledge.getUserId());
        response.setCreatedAt(knowledge.getCreatedAt());
        response.setUpdatedAt(knowledge.getUpdatedAt());
        return response;
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "BaseKnowledgeResponse{" +
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", type='" + type + '\'' +
                ", tags=" + tags +
                ", difficulty=" + difficulty +
                ", estimatedTime=" + estimatedTime +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                ", userId='" + userId + '\'' +
                '}';
    }
} 