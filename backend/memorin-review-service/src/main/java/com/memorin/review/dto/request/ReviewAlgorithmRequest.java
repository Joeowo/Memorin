package com.memorin.review.dto.request;

import javax.validation.constraints.*;

/**
 * SM-2算法计算请求DTO
 * 用于接收复习算法计算所需的参数
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class ReviewAlgorithmRequest {

    /**
     * 当前易度因子 (1.3-3.0)
     * 表示该知识点的记忆难易度，越高表示越容易记住
     */
    @NotNull(message = "易度因子不能为空")
    @DecimalMin(value = "1.3", message = "易度因子不能小于1.3")
    @DecimalMax(value = "3.0", message = "易度因子不能大于3.0")
    private Double easeFactor;

    /**
     * 当前复习间隔 (天数)
     * 表示上次复习到这次复习的间隔天数
     */
    @NotNull(message = "复习间隔不能为空")
    @Min(value = 1, message = "复习间隔不能小于1天")
    @Max(value = 365, message = "复习间隔不能超过365天")
    private Integer interval;

    /**
     * 质量评分 (1-3)
     * 1=错误, 2=模糊/困难, 3=正确/容易
     */
    @NotNull(message = "质量评分不能为空")
    @Min(value = 1, message = "质量评分不能小于1")
    @Max(value = 3, message = "质量评分不能大于3")
    private Integer quality;

    /**
     * 知识点ID (可选)
     * 用于日志记录和统计分析
     */
    @Size(max = 50, message = "知识点ID长度不能超过50个字符")
    private String knowledgePointId;

    /**
     * 用户ID (可选)
     * 用于个性化算法调整
     */
    @Size(max = 50, message = "用户ID长度不能超过50个字符")
    private String userId;

    // 构造函数
    public ReviewAlgorithmRequest() {}

    public ReviewAlgorithmRequest(Double easeFactor, Integer interval, Integer quality) {
        this.easeFactor = easeFactor;
        this.interval = interval;
        this.quality = quality;
    }

    // Getter和Setter方法
    public Double getEaseFactor() {
        return easeFactor;
    }

    public void setEaseFactor(Double easeFactor) {
        this.easeFactor = easeFactor;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ReviewAlgorithmRequest{" +
                "easeFactor=" + easeFactor +
                ", interval=" + interval +
                ", quality=" + quality +
                ", knowledgePointId='" + knowledgePointId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
} 