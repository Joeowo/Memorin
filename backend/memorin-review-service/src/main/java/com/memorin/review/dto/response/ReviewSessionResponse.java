package com.memorin.review.dto.response;

import com.memorin.review.entity.ReviewSession;
import java.util.List;

/**
 * 复习会话响应DTO
 * 返回复习会话的详细信息和统计数据
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class ReviewSessionResponse {

    /**
     * 会话唯一标识符
     */
    private String id;

    /**
     * 关联用户ID
     */
    private String userId;

    /**
     * 复习模式
     */
    private String reviewMode;

    /**
     * 会话配置JSON
     */
    private String sessionConfig;

    /**
     * 题目总数
     */
    private Integer totalQuestions;

    /**
     * 当前题目索引
     */
    private Integer currentIndex;

    /**
     * 已完成题目数量
     */
    private Integer completedCount;

    /**
     * 正确题目数量
     */
    private Integer correctCount;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 会话开始时间戳
     */
    private Long startTime;

    /**
     * 会话结束时间戳
     */
    private Long endTime;

    /**
     * 会话暂停时间戳
     */
    private Long pauseTime;

    /**
     * 累计暂停时长(毫秒)
     */
    private Long totalPauseDuration;

    /**
     * 预估总时长(分钟)
     */
    private Integer estimatedDurationMinutes;

    /**
     * 题目列表JSON
     */
    private String questionList;

    /**
     * 会话备注
     */
    private String notes;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 创建时间戳
     */
    private Long createdAt;

    /**
     * 更新时间戳
     */
    private Long updatedAt;

    /**
     * 计算得出的进度百分比
     */
    private Double progressPercentage;

    /**
     * 计算得出的正确率
     */
    private Double accuracyRate;

    /**
     * 实际会话时长(分钟)
     */
    private Long actualDurationMinutes;

    /**
     * 剩余题目数量
     */
    private Integer remainingQuestions;

    /**
     * 错误题目数量
     */
    private Integer wrongCount;

    /**
     * 预估剩余时间(分钟)
     */
    private Integer estimatedRemainingMinutes;

    /**
     * 当前题目信息(可选)
     */
    private CurrentQuestionInfo currentQuestion;

    /**
     * 会话统计信息
     */
    private SessionStatistics statistics;

    // 内部类：当前题目信息
    public static class CurrentQuestionInfo {
        private String knowledgePointId;
        private String questionType;
        private Integer difficulty;
        private String question;
        private List<String> tags;

        // Getter和Setter方法
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

        public Integer getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(Integer difficulty) {
            this.difficulty = difficulty;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    // 内部类：会话统计信息
    public static class SessionStatistics {
        private Double avgTimePerQuestion;
        private Integer fastAnswers;
        private Integer slowAnswers;
        private Double improvementRate;
        private String performanceLevel;

        // Getter和Setter方法
        public Double getAvgTimePerQuestion() {
            return avgTimePerQuestion;
        }

        public void setAvgTimePerQuestion(Double avgTimePerQuestion) {
            this.avgTimePerQuestion = avgTimePerQuestion;
        }

        public Integer getFastAnswers() {
            return fastAnswers;
        }

        public void setFastAnswers(Integer fastAnswers) {
            this.fastAnswers = fastAnswers;
        }

        public Integer getSlowAnswers() {
            return slowAnswers;
        }

        public void setSlowAnswers(Integer slowAnswers) {
            this.slowAnswers = slowAnswers;
        }

        public Double getImprovementRate() {
            return improvementRate;
        }

        public void setImprovementRate(Double improvementRate) {
            this.improvementRate = improvementRate;
        }

        public String getPerformanceLevel() {
            return performanceLevel;
        }

        public void setPerformanceLevel(String performanceLevel) {
            this.performanceLevel = performanceLevel;
        }
    }

    // 构造函数
    public ReviewSessionResponse() {}

    /**
     * 从ReviewSession实体转换为ReviewSessionResponse
     */
    public static ReviewSessionResponse fromEntity(ReviewSession session) {
        ReviewSessionResponse response = new ReviewSessionResponse();
        response.setId(session.getId());
        response.setUserId(session.getUserId());
        response.setReviewMode(session.getReviewMode());
        response.setSessionConfig(session.getSessionConfig());
        response.setTotalQuestions(session.getTotalQuestions());
        response.setCurrentIndex(session.getCurrentIndex());
        response.setCompletedCount(session.getCompletedCount());
        response.setCorrectCount(session.getCorrectCount());
        response.setStatus(session.getStatus());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setPauseTime(session.getPauseTime());
        response.setTotalPauseDuration(session.getTotalPauseDuration());
        response.setEstimatedDurationMinutes(session.getEstimatedDurationMinutes());
        response.setQuestionList(session.getQuestionList());
        response.setNotes(session.getNotes());
        response.setIsActive(session.getIsActive());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());
        
        // 计算衍生字段
        response.setProgressPercentage(session.getProgressPercentage());
        response.setAccuracyRate(session.getAccuracyRate());
        response.setActualDurationMinutes(session.getActualDurationMinutes());
        response.setRemainingQuestions(session.getTotalQuestions() - session.getCompletedCount());
        response.setWrongCount(session.getCompletedCount() - session.getCorrectCount());
        
        // 计算预估剩余时间
        if (session.getEstimatedDurationMinutes() != null && session.getActualDurationMinutes() != null) {
            long remaining = Math.max(0, session.getEstimatedDurationMinutes() - session.getActualDurationMinutes());
            response.setEstimatedRemainingMinutes((int) remaining);
        }
        
        return response;
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

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public Double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(Double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public Long getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Long actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public Integer getRemainingQuestions() {
        return remainingQuestions;
    }

    public void setRemainingQuestions(Integer remainingQuestions) {
        this.remainingQuestions = remainingQuestions;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public Integer getEstimatedRemainingMinutes() {
        return estimatedRemainingMinutes;
    }

    public void setEstimatedRemainingMinutes(Integer estimatedRemainingMinutes) {
        this.estimatedRemainingMinutes = estimatedRemainingMinutes;
    }

    public CurrentQuestionInfo getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(CurrentQuestionInfo currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public SessionStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(SessionStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return "ReviewSessionResponse{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", reviewMode='" + reviewMode + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", currentIndex=" + currentIndex +
                ", completedCount=" + completedCount +
                ", correctCount=" + correctCount +
                ", status='" + status + '\'' +
                ", progressPercentage=" + progressPercentage +
                ", accuracyRate=" + accuracyRate +
                ", actualDurationMinutes=" + actualDurationMinutes +
                '}';
    }
} 