package com.memorin.knowledge.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 基础知识点实体 - 只包含通用字段
 * 替代原有的复杂知识点结构，支持多种题型的统一管理
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "base_knowledge", indexes = {
    @Index(name = "idx_knowledge_user_id", columnList = "user_id"),
    @Index(name = "idx_knowledge_category_id", columnList = "category_id"),
    @Index(name = "idx_knowledge_type", columnList = "type"),
    @Index(name = "idx_knowledge_status", columnList = "status"),
    @Index(name = "idx_knowledge_difficulty", columnList = "difficulty")
})
public class BaseKnowledge {

    /**
     * 知识点唯一标识符
     * 格式: KP_{type}_{date}_{seq}
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 问题内容 - 主要展示内容
     */
    @NotBlank(message = "问题内容不能为空")
    @Size(max = 2000, message = "问题内容长度不能超过2000个字符")
    @Column(name = "question", length = 2000, nullable = false)
    private String question;

    /**
     * 答案解析 - 详细的解释说明
     */
    @Size(max = 5000, message = "答案解析长度不能超过5000个字符")
    @Column(name = "explanation", length = 5000)
    private String explanation;

    /**
     * 所属分类ID
     */
    @NotBlank(message = "所属分类ID不能为空")
    @Column(name = "category_id", length = 50, nullable = false)
    private String categoryId;

    /**
     * 题目类型 - text:文本题, choice:选择题, code:编程题
     */
    @NotBlank(message = "题目类型不能为空")
    @Pattern(regexp = "^(text|choice|code)$", message = "题目类型必须是text、choice或code")
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    /**
     * 标签数组 - 存储为逗号分隔的字符串
     */
    @Size(max = 500, message = "标签总长度不能超过500个字符")
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 难度等级 - 1到5级
     */
    @NotNull(message = "难度等级不能为空")
    @Min(value = 1, message = "难度等级不能小于1")
    @Max(value = 5, message = "难度等级不能大于5")
    @Column(name = "difficulty", nullable = false)
    private Integer difficulty;

    /**
     * 预估学习时间(分钟)
     */
    @NotNull(message = "预估学习时间不能为空")
    @Min(value = 1, message = "预估学习时间不能小于1分钟")
    @Max(value = 600, message = "预估学习时间不能超过600分钟")
    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime;

    /**
     * 知识点状态 - draft:草稿, published:已发布, archived:已归档
     */
    @NotBlank(message = "知识点状态不能为空")
    @Pattern(regexp = "^(draft|published|archived)$", message = "知识点状态必须是draft、published或archived")
    @Column(name = "status", length = 20, nullable = false)
    private String status = "draft";

    /**
     * 是否激活 - 支持软删除
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 关联用户ID - 多用户支持
     */
    @NotBlank(message = "用户ID不能为空")
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

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
    public BaseKnowledge() {
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
     * 获取标签列表 - 将逗号分隔的字符串转换为List
     */
    public List<String> getTagList() {
        List<String> tagList = new ArrayList<>();
        if (tags != null && !tags.trim().isEmpty()) {
            String[] tagArray = tags.split(",");
            for (String tag : tagArray) {
                String trimmedTag = tag.trim();
                if (!trimmedTag.isEmpty()) {
                    tagList.add(trimmedTag);
                }
            }
        }
        return tagList;
    }

    /**
     * 设置标签列表 - 将List转换为逗号分隔的字符串
     */
    public void setTagList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = "";
        } else {
            this.tags = String.join(",", tagList);
        }
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return "BaseKnowledge{" +
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", type='" + type + '\'' +
                ", difficulty=" + difficulty +
                ", estimatedTime=" + estimatedTime +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                ", userId='" + userId + '\'' +
                '}';
    }
} 