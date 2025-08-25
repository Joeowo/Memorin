package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.ScoringPoint;

import java.util.List;

/**
 * 评分要点响应DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class ScoringPointResponse {

    /**
     * 要点描述
     */
    private String description;

    /**
     * 要点分值
     */
    private Integer points;

    /**
     * 关键词列表
     */
    private List<String> keywords;

    // 构造函数
    public ScoringPointResponse() {}

    public ScoringPointResponse(String description, Integer points, List<String> keywords) {
        this.description = description;
        this.points = points;
        this.keywords = keywords;
    }

    /**
     * 从ScoringPoint实体转换为ScoringPointResponse
     */
    public static ScoringPointResponse fromEntity(ScoringPoint scoringPoint) {
        ScoringPointResponse response = new ScoringPointResponse();
        response.setDescription(scoringPoint.getDescription());
        response.setPoints(scoringPoint.getPoints());
        response.setKeywords(scoringPoint.getKeywordList()); // 使用实体的getKeywordList方法
        return response;
    }

    // Getter和Setter方法
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "ScoringPointResponse{" +
                "description='" + description + '\'' +
                ", points=" + points +
                ", keywords=" + keywords +
                '}';
    }
} 