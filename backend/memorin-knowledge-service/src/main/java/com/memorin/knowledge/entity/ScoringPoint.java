package com.memorin.knowledge.entity;

import javax.persistence.Embeddable;
import javax.persistence.Column;
import javax.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 评分要点嵌入式实体 - 用于问答题的评分标准
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Embeddable
public class ScoringPoint {

    /**
     * 要点描述
     */
    @NotBlank(message = "要点描述不能为空")
    @Size(max = 500, message = "要点描述长度不能超过500个字符")
    @Column(name = "description", length = 500, nullable = false)
    private String description;

    /**
     * 要点分值
     */
    @NotNull(message = "要点分值不能为空")
    @Min(value = 1, message = "要点分值不能小于1")
    @Max(value = 100, message = "要点分值不能大于100")
    @Column(name = "points", nullable = false)
    private Integer points;

    /**
     * 关键词 - 存储为逗号分隔的字符串
     */
    @Size(max = 1000, message = "关键词总长度不能超过1000个字符")
    @Column(name = "keywords", length = 1000)
    private String keywords;

    // 构造函数
    public ScoringPoint() {}

    public ScoringPoint(String description, Integer points, List<String> keywords) {
        this.description = description;
        this.points = points;
        this.setKeywordList(keywords);
    }

    /**
     * 获取关键词列表 - 将逗号分隔的字符串转换为List
     */
    public List<String> getKeywordList() {
        List<String> keywordList = new ArrayList<>();
        if (keywords != null && !keywords.trim().isEmpty()) {
            String[] keywordArray = keywords.split(",");
            for (String keyword : keywordArray) {
                String trimmedKeyword = keyword.trim();
                if (!trimmedKeyword.isEmpty()) {
                    keywordList.add(trimmedKeyword);
                }
            }
        }
        return keywordList;
    }

    /**
     * 设置关键词列表 - 将List转换为逗号分隔的字符串
     */
    public void setKeywordList(List<String> keywordList) {
        if (keywordList == null || keywordList.isEmpty()) {
            this.keywords = "";
        } else {
            this.keywords = String.join(",", keywordList);
        }
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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "ScoringPoint{" +
                "description='" + description + '\'' +
                ", points=" + points +
                ", keywords='" + keywords + '\'' +
                '}';
    }
} 