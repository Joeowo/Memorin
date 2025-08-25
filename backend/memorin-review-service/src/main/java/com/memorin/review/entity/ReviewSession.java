package com.memorin.review.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 复习会话实体
 * 管理用户的复习会话状态，包括配置、进度、时间等信息
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "review_sessions", indexes = {
    @Index(name = "idx_session_user_id", columnList = "user_id"),
    @Index(name = "idx_session_status", columnList = "status"),
    @Index(name = "idx_session_mode", columnList = "review_mode"),
    @Index(name = "idx_session_created", columnList = "created_at")
})
public class ReviewSession {

    /**
     * 会话唯一标识符
     * 格式: RS_{date}_{seq}
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 关联用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    /**
     * 复习模式
     * 如：smart-review, knowledge-base, knowledge-area, mistakes-review
     */
    @NotBlank(message = "复习模式不能为空")
    @Size(max = 50, message = "复习模式长度不能超过50个字符")
    @Column(name = "review_mode", length = 50, nullable = false)
    private String reviewMode;

    /**
     * 会话配置JSON
     * 存储复习配置参数，如数据源、过滤条件、排序规则等
     */
    @Size(max = 2000, message = "会话配置长度不能超过2000个字符")
    @Column(name = "session_config", length = 2000)
    private String sessionConfig;

    /**
     * 题目总数
     */
    @NotNull(message = "题目总数不能为空")
    @Min(value = 1, message = "题目总数不能小于1")
    @Max(value = 1000, message = "题目总数不能超过1000")
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    /**
     * 当前题目索引 (从0开始)
     */
    @NotNull(message = "当前题目索引不能为空")
    @Min(value = 0, message = "当前题目索引不能小于0")
    @Column(name = "current_index", nullable = false)
    private Integer currentIndex = 0;

    /**
     * 已完成题目数量
     */
    @NotNull(message = "已完成题目数量不能为空")
    @Min(value = 0, message = "已完成题目数量不能小于0")
    @Column(name = "completed_count", nullable = false)
    private Integer completedCount = 0;

    /**
     * 正确题目数量
     */
    @NotNull(message = "正确题目数量不能为空")
    @Min(value = 0, message = "正确题目数量不能小于0")
    @Column(name = "correct_count", nullable = false)
    private Integer correctCount = 0;

    /**
     * 会话状态
     * CREATED, ACTIVE, PAUSED, COMPLETED, CANCELLED
     */
    @NotBlank(message = "会话状态不能为空")
    @Pattern(regexp = "^(CREATED|ACTIVE|PAUSED|COMPLETED|CANCELLED)$", 
             message = "会话状态必须是CREATED、ACTIVE、PAUSED、COMPLETED或CANCELLED")
    @Column(name = "status", length = 20, nullable = false)
    private String status = "CREATED";

    /**
     * 会话开始时间戳
     */
    @Column(name = "start_time")
    private Long startTime;

    /**
     * 会话结束时间戳
     */
    @Column(name = "end_time")
    private Long endTime;

    /**
     * 会话暂停时间戳
     */
    @Column(name = "pause_time")
    private Long pauseTime;

    /**
     * 累计暂停时长(毫秒)
     */
    @Column(name = "total_pause_duration")
    private Long totalPauseDuration = 0L;

    /**
     * 预估总时长(分钟)
     * 基于题目数量和预估时间计算
     */
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    /**
     * 题目列表JSON
     * 存储知识点ID列表和题型信息
     */
    @Size(max = 5000, message = "题目列表长度不能超过5000个字符")
    @Column(name = "question_list", length = 5000)
    private String questionList;

    /**
     * 会话备注
     */
    @Size(max = 500, message = "会话备注长度不能超过500个字符")
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * 是否激活
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 创建时间戳
     */
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    /**
     * 更新时间戳
     */
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    // 构造函数
    public ReviewSession() {
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    // JPA生命周期回调
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 启动会话
     */
    public void startSession() {
        this.status = "ACTIVE";
        this.startTime = System.currentTimeMillis();
        this.pauseTime = null;
    }

    /**
     * 暂停会话
     */
    public void pauseSession() {
        if ("ACTIVE".equals(this.status)) {
            this.status = "PAUSED";
            this.pauseTime = System.currentTimeMillis();
        }
    }

    /**
     * 恢复会话
     */
    public void resumeSession() {
        if ("PAUSED".equals(this.status)) {
            this.status = "ACTIVE";
            if (this.pauseTime != null) {
                long pauseDuration = System.currentTimeMillis() - this.pauseTime;
                this.totalPauseDuration += pauseDuration;
                this.pauseTime = null;
            }
        }
    }

    /**
     * 完成会话
     */
    public void completeSession() {
        this.status = "COMPLETED";
        this.endTime = System.currentTimeMillis();
        this.pauseTime = null;
    }

    /**
     * 取消会话
     */
    public void cancelSession() {
        this.status = "CANCELLED";
        this.endTime = System.currentTimeMillis();
        this.pauseTime = null;
    }

    /**
     * 计算会话进度百分比
     */
    public Double getProgressPercentage() {
        if (totalQuestions == null || totalQuestions == 0) {
            return 0.0;
        }
        return (double) completedCount / totalQuestions * 100;
    }

    /**
     * 计算正确率
     */
    public Double getAccuracyRate() {
        if (completedCount == null || completedCount == 0) {
            return 0.0;
        }
        return (double) correctCount / completedCount;
    }

    /**
     * 计算实际会话时长(分钟)
     */
    public Long getActualDurationMinutes() {
        if (startTime == null) {
            return 0L;
        }
        
        long endTimeToUse = endTime != null ? endTime : System.currentTimeMillis();
        long totalDuration = endTimeToUse - startTime;
        
        // 减去暂停时长
        if (totalPauseDuration != null) {
            totalDuration -= totalPauseDuration;
        }
        
        // 如果当前是暂停状态，减去当前暂停时长
        if ("PAUSED".equals(status) && pauseTime != null) {
            totalDuration -= (System.currentTimeMillis() - pauseTime);
        }
        
        return Math.max(0, totalDuration / (60 * 1000)); // 转换为分钟
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

    public String getReviewMode() {
        return reviewMode;
    }

    public void setReviewMode(String reviewMode) {
        this.reviewMode = reviewMode;
    }

    public String getSessionConfig() {
        return sessionConfig;
    }

    public void setSessionConfig(String sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(Integer currentIndex) {
        this.currentIndex = currentIndex;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(Long pauseTime) {
        this.pauseTime = pauseTime;
    }

    public Long getTotalPauseDuration() {
        return totalPauseDuration;
    }

    public void setTotalPauseDuration(Long totalPauseDuration) {
        this.totalPauseDuration = totalPauseDuration;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public String getQuestionList() {
        return questionList;
    }

    public void setQuestionList(String questionList) {
        this.questionList = questionList;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        return "ReviewSession{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", reviewMode='" + reviewMode + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", currentIndex=" + currentIndex +
                ", completedCount=" + completedCount +
                ", correctCount=" + correctCount +
                ", status='" + status + '\'' +
                ", progressPercentage=" + getProgressPercentage() +
                ", accuracyRate=" + getAccuracyRate() +
                '}';
    }
} 