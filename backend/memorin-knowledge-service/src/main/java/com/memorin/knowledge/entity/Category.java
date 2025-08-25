package com.memorin.knowledge.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 知识分类实体 - 统一的层级分类系统
 * 替代原有的 KnowledgeBase + KnowledgeArea 双层结构
 * 支持无限层级，推荐使用3级：学科（知识库） → 章节（知识区） → 小节（子知识区）
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_user_id", columnList = "user_id"),
    @Index(name = "idx_category_parent_id", columnList = "parent_id"),
    @Index(name = "idx_category_level", columnList = "level"),
    @Index(name = "idx_category_sort_order", columnList = "sort_order")
})
public class Category {

    /**
     * 分类唯一标识符
     * 格式: CAT_{level}_{seq}_{hash}
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 分类名称 - 用于显示
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * 父分类ID - null表示顶级分类
     */
    @Column(name = "parent_id", length = 50)
    private String parentId;

    /**
     * 分类层级 - 1:学科级, 2:章节级, 3:小节级, 4+:自定义
     */
    @Min(value = 1, message = "分类层级不能小于1")
    @Max(value = 5, message = "分类层级不能大于5")
    @Column(name = "level", nullable = false)
    private Integer level;

    /**
     * 完整分类路径 - 如"数学/高等数学/极限理论"
     */
    @NotBlank(message = "分类路径不能为空")
    @Size(max = 500, message = "分类路径长度不能超过500个字符")
    @Column(name = "path", length = 500, nullable = false)
    private String path;

    /**
     * 分类描述 - 可选的详细说明
     */
    @Size(max = 1000, message = "分类描述长度不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * UI显示颜色 - hex格式，如"#667eea"
     */
    @NotBlank(message = "显示颜色不能为空")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式必须为hex格式，如#667eea")
    @Column(name = "color", length = 7, nullable = false)
    private String color;

    /**
     * UI显示图标 - emoji或图标类名
     */
    @NotBlank(message = "显示图标不能为空")
    @Size(max = 50, message = "图标长度不能超过50个字符")
    @Column(name = "icon", length = 50, nullable = false)
    private String icon;

    /**
     * 同级排序权重 - 数值越小越靠前
     */
    @Min(value = 0, message = "排序权重不能为负数")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

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
    public Category() {
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    // JPA生命周期回调
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", level=" + level +
                ", path='" + path + '\'' +
                ", color='" + color + '\'' +
                ", icon='" + icon + '\'' +
                ", sortOrder=" + sortOrder +
                ", isActive=" + isActive +
                ", userId='" + userId + '\'' +
                '}';
    }
} 