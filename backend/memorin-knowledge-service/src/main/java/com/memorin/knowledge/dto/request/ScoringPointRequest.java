package com.memorin.knowledge.dto.request;

import javax.validation.constraints.*;
import java.util.List;

/**
 * 评分要点请求DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class ScoringPointRequest {

    /**
     * 要点描述
     */
    @NotBlank(message = "要点描述不能为空")
    @Size(max = 500, message = "要点描述长度不能超过500个字符")
    private String description;

    /**
     * 要点分值
     */
    @NotNull(message = "要点分值不能为空")
    @Min(value = 1, message = "要点分值不能小于1")
    @Max(value = 100, message = "要点分值不能大于100")
    private Integer points;

    /**
     * 关键词列表
     */
    @Size(max = 20, message = "关键词数量不能超过20个")
    private List<String> keywords;

    // 构造函数
    public ScoringPointRequest() {}

    public ScoringPointRequest(String description, Integer points, List<String> keywords) {
        this.description = description;
        this.points = points;
        this.keywords = keywords;
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
        return "ScoringPointRequest{" +
                "description='" + description + '\'' +
                ", points=" + points +
                ", keywords=" + keywords +
                '}';
    }
} 