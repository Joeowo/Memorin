package com.memorin.review.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 错题记录实体
 * 对应原系统storage.js中的mistake记录结构
 */
@Entity
@Table(name = "mistake_records")
public class MistakeRecord {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    /**
     * 关联的知识点ID（来自knowledge-service）
     */
    @Column(name = "knowledge_point_id", nullable = false, length = 50)
    private String knowledgePointId;
    
    /**
     * 错误次数统计
     */
    @Column(name = "mistake_count", nullable = false)
    private Integer mistakeCount = 1;
    
    /**
     * 首次错误时间
     */
    @Column(name = "first_mistake_time", nullable = false)
    private LocalDateTime firstMistakeTime;
    
    /**
     * 最近错误时间
     */
    @Column(name = "last_mistake_time", nullable = false)
    private LocalDateTime lastMistakeTime;
    
    /**
     * 错误原因列表（JSON格式存储）
     */
    @Column(name = "mistake_reasons", columnDefinition = "TEXT")
    private String mistakeReasons; // JSON数组字符串
    
    /**
     * 是否已解决
     */
    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;
    
    /**
     * 解决时间
     */
    @Column(name = "resolved_time")
    private LocalDateTime resolvedTime;
    
    /**
     * 题目类型（text, choice, code等）
     */
    @Column(name = "question_type", length = 20)
    private String questionType;
    
    /**
     * 难度等级
     */
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;
    
    /**
     * 总复习次数（包括正确和错误）
     */
    @Column(name = "total_review_count")
    private Integer totalReviewCount = 0;
    
    /**
     * 正确次数
     */
    @Column(name = "correct_count")
    private Integer correctCount = 0;
    
    /**
     * 错误标签（可用于分类错误类型）
     */
    @Column(name = "mistake_tags", length = 500)
    private String mistakeTags;
    
    /**
     * 用户备注
     */
    @Column(name = "user_notes", length = 1000)
    private String userNotes;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 构造函数
    public MistakeRecord() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.firstMistakeTime = LocalDateTime.now();
        this.lastMistakeTime = LocalDateTime.now();
    }
    
    public MistakeRecord(String id, String userId, String knowledgePointId) {
        this();
        this.id = id;
        this.userId = userId;
        this.knowledgePointId = knowledgePointId;
    }
    
    // 业务方法
    
    /**
     * 增加错误次数
     */
    public void incrementMistakeCount(String reason) {
        this.mistakeCount++;
        this.lastMistakeTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // 添加错误原因（简单实现，后续可以改为JSON处理）
        if (this.mistakeReasons == null || this.mistakeReasons.isEmpty()) {
            this.mistakeReasons = "[\"" + reason + "\"]";
        } else {
            // 简单的JSON数组追加
            this.mistakeReasons = this.mistakeReasons.substring(0, this.mistakeReasons.length() - 1) 
                                + ",\"" + reason + "\"]";
        }
    }
    
    /**
     * 标记为已解决
     */
    public void markAsResolved() {
        this.isResolved = true;
        this.resolvedTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 重新标记为未解决
     */
    public void markAsUnresolved() {
        this.isResolved = false;
        this.resolvedTime = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 增加复习次数统计
     */
    public void incrementReviewCount(boolean isCorrect) {
        this.totalReviewCount++;
        if (isCorrect) {
            this.correctCount++;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 计算错误率
     */
    public double getErrorRate() {
        if (totalReviewCount == 0) return 0.0;
        return (double) mistakeCount / totalReviewCount;
    }
    
    /**
     * 计算正确率
     */
    public double getAccuracyRate() {
        if (totalReviewCount == 0) return 0.0;
        return (double) correctCount / totalReviewCount;
    }
    
    /**
     * 获取掌握状态描述
     */
    public String getMasteryStatus() {
        if (isResolved) return "已掌握";
        
        double accuracy = getAccuracyRate();
        if (accuracy >= 0.8) return "接近掌握";
        if (accuracy >= 0.6) return "部分掌握";
        if (accuracy >= 0.3) return "需要练习";
        return "需要重点复习";
    }
    
    // Getter和Setter方法
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getKnowledgePointId() {
        return knowledgePointId;
    }
    
    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }
    
    public Integer getMistakeCount() {
        return mistakeCount;
    }
    
    public void setMistakeCount(Integer mistakeCount) {
        this.mistakeCount = mistakeCount;
    }
    
    public LocalDateTime getFirstMistakeTime() {
        return firstMistakeTime;
    }
    
    public void setFirstMistakeTime(LocalDateTime firstMistakeTime) {
        this.firstMistakeTime = firstMistakeTime;
    }
    
    public LocalDateTime getLastMistakeTime() {
        return lastMistakeTime;
    }
    
    public void setLastMistakeTime(LocalDateTime lastMistakeTime) {
        this.lastMistakeTime = lastMistakeTime;
    }
    
    public String getMistakeReasons() {
        return mistakeReasons;
    }
    
    public void setMistakeReasons(String mistakeReasons) {
        this.mistakeReasons = mistakeReasons;
    }
    
    public Boolean getIsResolved() {
        return isResolved;
    }
    
    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }
    
    public LocalDateTime getResolvedTime() {
        return resolvedTime;
    }
    
    public void setResolvedTime(LocalDateTime resolvedTime) {
        this.resolvedTime = resolvedTime;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public String getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    public Integer getTotalReviewCount() {
        return totalReviewCount;
    }
    
    public void setTotalReviewCount(Integer totalReviewCount) {
        this.totalReviewCount = totalReviewCount;
    }
    
    public Integer getCorrectCount() {
        return correctCount;
    }
    
    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }
    
    public String getMistakeTags() {
        return mistakeTags;
    }
    
    public void setMistakeTags(String mistakeTags) {
        this.mistakeTags = mistakeTags;
    }
    
    public String getUserNotes() {
        return userNotes;
    }
    
    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 