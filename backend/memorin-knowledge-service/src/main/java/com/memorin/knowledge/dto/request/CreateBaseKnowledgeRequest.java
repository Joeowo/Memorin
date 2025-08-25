package com.memorin.knowledge.dto.request;

import javax.validation.constraints.*;
import java.util.List;

/**
 * 创建基础知识点请求DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class CreateBaseKnowledgeRequest {

    /**
     * 问题内容 - 主要展示内容
     */
    @NotBlank(message = "问题内容不能为空")
    @Size(max = 2000, message = "问题内容长度不能超过2000个字符")
    private String question;

    /**
     * 答案解析 - 详细的解释说明（可选）
     */
    @Size(max = 5000, message = "答案解析长度不能超过5000个字符")
    private String explanation;

    /**
     * 所属分类ID
     */
    @NotBlank(message = "所属分类ID不能为空")
    @Size(max = 50, message = "分类ID长度不能超过50个字符")
    private String categoryId;

    /**
     * 题目类型 - text:文本题, choice:选择题, code:编程题
     */
    @NotBlank(message = "题目类型不能为空")
    @Pattern(regexp = "^(text|choice|code)$", message = "题目类型必须是text、choice或code")
    private String type;

    /**
     * 标签列表
     */
    @Size(max = 10, message = "标签数量不能超过10个")
    private List<String> tags;

    /**
     * 难度等级 - 1到5级
     */
    @NotNull(message = "难度等级不能为空")
    @Min(value = 1, message = "难度等级不能小于1")
    @Max(value = 5, message = "难度等级不能大于5")
    private Integer difficulty;

    /**
     * 预估学习时间(分钟)
     */
    @NotNull(message = "预估学习时间不能为空")
    @Min(value = 1, message = "预估学习时间不能小于1分钟")
    @Max(value = 600, message = "预估学习时间不能超过600分钟")
    private Integer estimatedTime;

    /**
     * 知识点状态 - draft:草稿, published:已发布（可选，默认为draft）
     */
    @Pattern(regexp = "^(draft|published)$", message = "知识点状态必须是draft或published")
    private String status = "draft";

    // 构造函数
    public CreateBaseKnowledgeRequest() {}

    // Getter和Setter方法
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateBaseKnowledgeRequest{" +
                "question='" + question + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", type='" + type + '\'' +
                ", tags=" + tags +
                ", difficulty=" + difficulty +
                ", estimatedTime=" + estimatedTime +
                ", status='" + status + '\'' +
                '}';
    }
} 