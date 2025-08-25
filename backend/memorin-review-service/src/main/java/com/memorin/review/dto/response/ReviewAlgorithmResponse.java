package com.memorin.review.dto.response;

/**
 * SM-2算法计算响应DTO
 * 返回算法计算后的新参数和下次复习时间
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class ReviewAlgorithmResponse {

    /**
     * 新的易度因子 (1.3-3.0)
     * 根据本次复习质量调整后的难易度因子
     */
    private Double newEaseFactor;

    /**
     * 新的复习间隔 (天数)
     * 计算得出的下次复习间隔天数
     */
    private Integer newInterval;

    /**
     * 下次复习时间戳 (毫秒)
     * 具体的下次复习时间点
     */
    private Long nextReviewTime;

    /**
     * 复习强度级别
     * IMMEDIATE(立即), SHORT(短期), MEDIUM(中期), LONG(长期)
     */
    private String reviewIntensity;

    /**
     * 算法版本标识
     * 用于追踪和调试算法变更
     */
    private String algorithmVersion = "SM-2_v1.0";

    /**
     * 计算时间戳
     * 记录算法计算的时间
     */
    private Long calculatedAt;

    /**
     * 复习建议
     * 基于计算结果给出的学习建议
     */
    private String recommendation;

    // 构造函数
    public ReviewAlgorithmResponse() {
        this.calculatedAt = System.currentTimeMillis();
    }

    public ReviewAlgorithmResponse(Double newEaseFactor, Integer newInterval, Long nextReviewTime) {
        this();
        this.newEaseFactor = newEaseFactor;
        this.newInterval = newInterval;
        this.nextReviewTime = nextReviewTime;
        this.reviewIntensity = determineReviewIntensity(newInterval);
        this.recommendation = generateRecommendation(newEaseFactor, newInterval);
    }

    /**
     * 根据间隔天数确定复习强度
     */
    private String determineReviewIntensity(Integer interval) {
        if (interval <= 1) {
            return "IMMEDIATE";
        } else if (interval <= 7) {
            return "SHORT";
        } else if (interval <= 30) {
            return "MEDIUM";
        } else {
            return "LONG";
        }
    }

    /**
     * 生成复习建议
     */
    private String generateRecommendation(Double easeFactor, Integer interval) {
        if (easeFactor < 2.0) {
            return "这个知识点需要重点关注，建议加强练习";
        } else if (easeFactor < 2.5) {
            return "掌握程度一般，继续保持复习节奏";
        } else {
            return "掌握程度良好，可以适当延长复习间隔";
        }
    }

    // Getter和Setter方法
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
        this.reviewIntensity = determineReviewIntensity(newInterval);
    }

    public Long getNextReviewTime() {
        return nextReviewTime;
    }

    public void setNextReviewTime(Long nextReviewTime) {
        this.nextReviewTime = nextReviewTime;
    }

    public String getReviewIntensity() {
        return reviewIntensity;
    }

    public void setReviewIntensity(String reviewIntensity) {
        this.reviewIntensity = reviewIntensity;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    public void setAlgorithmVersion(String algorithmVersion) {
        this.algorithmVersion = algorithmVersion;
    }

    public Long getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Long calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public String toString() {
        return "ReviewAlgorithmResponse{" +
                "newEaseFactor=" + newEaseFactor +
                ", newInterval=" + newInterval +
                ", nextReviewTime=" + nextReviewTime +
                ", reviewIntensity='" + reviewIntensity + '\'' +
                ", algorithmVersion='" + algorithmVersion + '\'' +
                ", calculatedAt=" + calculatedAt +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
} 