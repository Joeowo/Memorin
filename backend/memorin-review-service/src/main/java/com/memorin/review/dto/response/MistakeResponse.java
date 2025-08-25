package com.memorin.review.dto.response;

import com.memorin.review.entity.MistakeRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 错题响应DTO
 */
public class MistakeResponse {

    /**
     * 错题记录ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 知识点ID
     */
    private String knowledgePointId;

    /**
     * 错误次数
     */
    private Integer mistakeCount;

    /**
     * 首次错误时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstMistakeTime;

    /**
     * 最近错误时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMistakeTime;

    /**
     * 错误原因列表
     */
    private String mistakeReasons;

    /**
     * 是否已解决
     */
    private Boolean isResolved;

    /**
     * 解决时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedTime;

    /**
     * 题目类型
     */
    private String questionType;

    /**
     * 难度等级
     */
    private String difficultyLevel;

    /**
     * 总复习次数
     */
    private Integer totalReviewCount;

    /**
     * 正确次数
     */
    private Integer correctCount;

    /**
     * 错误标签
     */
    private String mistakeTags;

    /**
     * 用户备注
     */
    private String userNotes;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 错误率
     */
    private Double errorRate;

    /**
     * 正确率
     */
    private Double accuracyRate;

    /**
     * 掌握状态
     */
    private String masteryStatus;

    // 构造函数
    public MistakeResponse() {
    }

    /**
     * 从MistakeRecord实体创建响应DTO
     */
    public static MistakeResponse fromEntity(MistakeRecord mistakeRecord) {
        MistakeResponse response = new MistakeResponse();
        response.setId(mistakeRecord.getId());
        response.setUserId(mistakeRecord.getUserId());
        response.setKnowledgePointId(mistakeRecord.getKnowledgePointId());
        response.setMistakeCount(mistakeRecord.getMistakeCount());
        response.setFirstMistakeTime(mistakeRecord.getFirstMistakeTime());
        response.setLastMistakeTime(mistakeRecord.getLastMistakeTime());
        response.setMistakeReasons(mistakeRecord.getMistakeReasons());
        response.setIsResolved(mistakeRecord.getIsResolved());
        response.setResolvedTime(mistakeRecord.getResolvedTime());
        response.setQuestionType(mistakeRecord.getQuestionType());
        response.setDifficultyLevel(mistakeRecord.getDifficultyLevel());
        response.setTotalReviewCount(mistakeRecord.getTotalReviewCount());
        response.setCorrectCount(mistakeRecord.getCorrectCount());
        response.setMistakeTags(mistakeRecord.getMistakeTags());
        response.setUserNotes(mistakeRecord.getUserNotes());
        response.setCreatedAt(mistakeRecord.getCreatedAt());
        response.setUpdatedAt(mistakeRecord.getUpdatedAt());
        response.setErrorRate(mistakeRecord.getErrorRate());
        response.setAccuracyRate(mistakeRecord.getAccuracyRate());
        response.setMasteryStatus(mistakeRecord.getMasteryStatus());
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

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }

    public Double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(Double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public String getMasteryStatus() {
        return masteryStatus;
    }

    public void setMasteryStatus(String masteryStatus) {
        this.masteryStatus = masteryStatus;
    }

    /**
     * 错题统计响应内部类
     */
    public static class MistakeStatisticsResponse {
        private Long totalMistakes;
        private Long unresolvedMistakes;
        private Long resolvedMistakes;
        private Double averageMistakeCount;
        private Double averageAccuracyRate;
        private Long recentMistakes7Days;
        private Long recentMistakes30Days;
        private Long recentResolved7Days;
        private Double resolutionRate;
        private List<Object[]> mistakesByType;
        private List<Object[]> mistakesByDifficulty;

        // 构造函数
        public MistakeStatisticsResponse() {
        }

        public static MistakeStatisticsResponse fromMap(Map<String, Object> stats) {
            MistakeStatisticsResponse response = new MistakeStatisticsResponse();
            response.setTotalMistakes((Long) stats.get("totalMistakes"));
            response.setUnresolvedMistakes((Long) stats.get("unresolvedMistakes"));
            response.setResolvedMistakes((Long) stats.get("resolvedMistakes"));
            response.setAverageMistakeCount((Double) stats.get("averageMistakeCount"));
            response.setAverageAccuracyRate((Double) stats.get("averageAccuracyRate"));
            response.setRecentMistakes7Days((Long) stats.get("recentMistakes7Days"));
            response.setRecentMistakes30Days((Long) stats.get("recentMistakes30Days"));
            response.setRecentResolved7Days((Long) stats.get("recentResolved7Days"));
            response.setResolutionRate((Double) stats.get("resolutionRate"));
            response.setMistakesByType((List<Object[]>) stats.get("mistakesByType"));
            response.setMistakesByDifficulty((List<Object[]>) stats.get("mistakesByDifficulty"));
            return response;
        }

        // Getter和Setter方法
        public Long getTotalMistakes() {
            return totalMistakes;
        }

        public void setTotalMistakes(Long totalMistakes) {
            this.totalMistakes = totalMistakes;
        }

        public Long getUnresolvedMistakes() {
            return unresolvedMistakes;
        }

        public void setUnresolvedMistakes(Long unresolvedMistakes) {
            this.unresolvedMistakes = unresolvedMistakes;
        }

        public Long getResolvedMistakes() {
            return resolvedMistakes;
        }

        public void setResolvedMistakes(Long resolvedMistakes) {
            this.resolvedMistakes = resolvedMistakes;
        }

        public Double getAverageMistakeCount() {
            return averageMistakeCount;
        }

        public void setAverageMistakeCount(Double averageMistakeCount) {
            this.averageMistakeCount = averageMistakeCount;
        }

        public Double getAverageAccuracyRate() {
            return averageAccuracyRate;
        }

        public void setAverageAccuracyRate(Double averageAccuracyRate) {
            this.averageAccuracyRate = averageAccuracyRate;
        }

        public Long getRecentMistakes7Days() {
            return recentMistakes7Days;
        }

        public void setRecentMistakes7Days(Long recentMistakes7Days) {
            this.recentMistakes7Days = recentMistakes7Days;
        }

        public Long getRecentMistakes30Days() {
            return recentMistakes30Days;
        }

        public void setRecentMistakes30Days(Long recentMistakes30Days) {
            this.recentMistakes30Days = recentMistakes30Days;
        }

        public Long getRecentResolved7Days() {
            return recentResolved7Days;
        }

        public void setRecentResolved7Days(Long recentResolved7Days) {
            this.recentResolved7Days = recentResolved7Days;
        }

        public Double getResolutionRate() {
            return resolutionRate;
        }

        public void setResolutionRate(Double resolutionRate) {
            this.resolutionRate = resolutionRate;
        }

        public List<Object[]> getMistakesByType() {
            return mistakesByType;
        }

        public void setMistakesByType(List<Object[]> mistakesByType) {
            this.mistakesByType = mistakesByType;
        }

        public List<Object[]> getMistakesByDifficulty() {
            return mistakesByDifficulty;
        }

        public void setMistakesByDifficulty(List<Object[]> mistakesByDifficulty) {
            this.mistakesByDifficulty = mistakesByDifficulty;
        }
    }
} 