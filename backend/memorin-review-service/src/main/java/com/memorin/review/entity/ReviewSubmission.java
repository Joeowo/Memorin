package com.memorin.review.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 复习提交记录实体
 * 记录复习会话中每个知识点的答题详情和评分信息
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "review_submissions", indexes = {
    @Index(name = "idx_submission_session_id", columnList = "session_id"),
    @Index(name = "idx_submission_knowledge_id", columnList = "knowledge_point_id"),
    @Index(name = "idx_submission_created", columnList = "created_at"),
    @Index(name = "idx_submission_rating", columnList = "quality_rating")
})
public class ReviewSubmission {

    /**
     * 提交记录唯一标识符
     * 格式: RSB_{date}_{seq}
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 关联的复习会话ID
     */
    @NotBlank(message = "复习会话ID不能为空")
    @Column(name = "session_id", length = 50, nullable = false)
    private String sessionId;

    /**
     * 知识点ID
     */
    @NotBlank(message = "知识点ID不能为空")
    @Column(name = "knowledge_point_id", length = 50, nullable = false)
    private String knowledgePointId;

    /**
     * 题目类型
     * text:文本题, choice:选择题, code:编程题
     */
    @NotBlank(message = "题目类型不能为空")
    @Pattern(regexp = "^(text|choice|code)$", message = "题目类型必须是text、choice或code")
    @Column(name = "question_type", length = 20, nullable = false)
    private String questionType;

    /**
     * 题目在会话中的索引位置
     */
    @NotNull(message = "题目索引不能为空")
    @Min(value = 0, message = "题目索引不能小于0")
    @Column(name = "question_index", nullable = false)
    private Integer questionIndex;

    /**
     * 用户答案JSON
     * 存储用户的具体答案内容
     */
    @Size(max = 3000, message = "用户答案长度不能超过3000个字符")
    @Column(name = "user_answer", length = 3000)
    private String userAnswer;

    /**
     * 是否答对
     */
    @Column(name = "is_correct")
    private Boolean isCorrect;

    /**
     * 质量评分 (1-3)
     * 1=错误/困难, 2=模糊/一般, 3=正确/容易
     */
    @NotNull(message = "质量评分不能为空")
    @Min(value = 1, message = "质量评分不能小于1")
    @Max(value = 3, message = "质量评分不能大于3")
    @Column(name = "quality_rating", nullable = false)
    private Integer qualityRating;

    /**
     * 用时(秒)
     */
    @NotNull(message = "用时不能为空")
    @Min(value = 1, message = "用时不能小于1秒")
    @Max(value = 3600, message = "用时不能超过3600秒")
    @Column(name = "time_spent_seconds", nullable = false)
    private Integer timeSpentSeconds;

    /**
     * 提交前的易度因子
     */
    @Column(name = "previous_ease_factor")
    private Double previousEaseFactor;

    /**
     * 提交前的复习间隔
     */
    @Column(name = "previous_interval")
    private Integer previousInterval;

    /**
     * 计算后的新易度因子
     */
    @Column(name = "new_ease_factor")
    private Double newEaseFactor;

    /**
     * 计算后的新复习间隔
     */
    @Column(name = "new_interval")
    private Integer newInterval;

    /**
     * 下次复习时间戳
     */
    @Column(name = "next_review_time")
    private Long nextReviewTime;

    /**
     * 错误原因(如果答错)
     */
    @Size(max = 500, message = "错误原因长度不能超过500个字符")
    @Column(name = "mistake_reason", length = 500)
    private String mistakeReason;

    /**
     * 学习笔记
     */
    @Size(max = 1000, message = "学习笔记长度不能超过1000个字符")
    @Column(name = "study_notes", length = 1000)
    private String studyNotes;

    /**
     * 难度感知 (1-5)
     * 用户主观感受的题目难度
     */
    @Min(value = 1, message = "难度感知不能小于1")
    @Max(value = 5, message = "难度感知不能大于5")
    @Column(name = "perceived_difficulty")
    private Integer perceivedDifficulty;

    /**
     * 是否跳过
     */
    @Column(name = "is_skipped", nullable = false)
    private Boolean isSkipped = false;

    /**
     * 提交方式
     * NORMAL:正常提交, SKIP:跳过, AUTO:自动提交
     */
    @NotBlank(message = "提交方式不能为空")
    @Pattern(regexp = "^(NORMAL|SKIP|AUTO)$", message = "提交方式必须是NORMAL、SKIP或AUTO")
    @Column(name = "submission_type", length = 20, nullable = false)
    private String submissionType = "NORMAL";

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
    public ReviewSubmission() {
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
     * 标记为跳过
     */
    public void markAsSkipped() {
        this.isSkipped = true;
        this.submissionType = "SKIP";
        this.isCorrect = false;
        this.qualityRating = 1; // 跳过视为错误
    }

    /**
     * 根据正确性自动设置质量评分
     */
    public void autoSetQualityRating() {
        if (this.isCorrect != null) {
            this.qualityRating = this.isCorrect ? 3 : 1;
        }
    }

    /**
     * 计算提交效率
     * 基于用时和题目类型的预期时间
     */
    public Double getSubmissionEfficiency() {
        if (timeSpentSeconds == null || timeSpentSeconds == 0) {
            return 0.0;
        }
        
        // 根据题目类型设定预期时间(秒)
        int expectedTime;
        switch (questionType) {
            case "text":
                expectedTime = 60; // 文本题预期1分钟
                break;
            case "choice":
                expectedTime = 30; // 选择题预期30秒
                break;
            case "code":
                expectedTime = 180; // 编程题预期3分钟
                break;
            default:
                expectedTime = 60;
        }
        
        // 效率 = 预期时间 / 实际用时，大于1表示超时
        return (double) expectedTime / timeSpentSeconds;
    }

    /**
     * 获取学习反馈等级
     */
    public String getFeedbackLevel() {
        if (isCorrect == null) return "PENDING";
        
        if (isCorrect && timeSpentSeconds != null) {
            double efficiency = getSubmissionEfficiency();
            if (efficiency > 1.5) {
                return "EXCELLENT"; // 快速正确
            } else if (efficiency > 0.8) {
                return "GOOD"; // 正常正确
            } else {
                return "SLOW"; // 慢速正确
            }
        } else {
            return "WRONG"; // 错误
        }
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(Integer qualityRating) {
        this.qualityRating = qualityRating;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public Double getPreviousEaseFactor() {
        return previousEaseFactor;
    }

    public void setPreviousEaseFactor(Double previousEaseFactor) {
        this.previousEaseFactor = previousEaseFactor;
    }

    public Integer getPreviousInterval() {
        return previousInterval;
    }

    public void setPreviousInterval(Integer previousInterval) {
        this.previousInterval = previousInterval;
    }

    public Double getNewEaseFactor() {
        return newEaseFactor;
    }

    public void setNewEaseFactor(Double newEaseFactor) {
        this.newEaseFactor = newEaseFactor;
    }

    public Integer getNewInterval() {
        return newInterval;
    }

    public void setNewInterval(Integer newInterval) {
        this.newInterval = newInterval;
    }

    public Long getNextReviewTime() {
        return nextReviewTime;
    }

    public void setNextReviewTime(Long nextReviewTime) {
        this.nextReviewTime = nextReviewTime;
    }

    public String getMistakeReason() {
        return mistakeReason;
    }

    public void setMistakeReason(String mistakeReason) {
        this.mistakeReason = mistakeReason;
    }

    public String getStudyNotes() {
        return studyNotes;
    }

    public void setStudyNotes(String studyNotes) {
        this.studyNotes = studyNotes;
    }

    public Integer getPerceivedDifficulty() {
        return perceivedDifficulty;
    }

    public void setPerceivedDifficulty(Integer perceivedDifficulty) {
        this.perceivedDifficulty = perceivedDifficulty;
    }

    public Boolean getIsSkipped() {
        return isSkipped;
    }

    public void setIsSkipped(Boolean isSkipped) {
        this.isSkipped = isSkipped;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
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
        return "ReviewSubmission{" +
                "id='" + id + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", knowledgePointId='" + knowledgePointId + '\'' +
                ", questionType='" + questionType + '\'' +
                ", questionIndex=" + questionIndex +
                ", isCorrect=" + isCorrect +
                ", qualityRating=" + qualityRating +
                ", timeSpentSeconds=" + timeSpentSeconds +
                ", submissionType='" + submissionType + '\'' +
                ", feedbackLevel='" + getFeedbackLevel() + '\'' +
                '}';
    }
} 