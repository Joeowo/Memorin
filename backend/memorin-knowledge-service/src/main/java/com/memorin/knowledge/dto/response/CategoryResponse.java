package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.Category;

import java.util.List;
import java.util.ArrayList;

/**
 * 分类响应DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class CategoryResponse {

    /**
     * 分类唯一标识符
     */
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID
     */
    private String parentId;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 完整分类路径
     */
    private String path;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 显示颜色
     */
    private String color;

    /**
     * 显示图标
     */
    private String icon;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 关联用户ID
     */
    private String userId;

    /**
     * 创建时间戳
     */
    private Long createdAt;

    /**
     * 更新时间戳
     */
    private Long updatedAt;

    /**
     * 子分类列表（用于树形结构）
     */
    private List<CategoryResponse> children;

    /**
     * 子分类数量
     */
    private Integer childrenCount;

    // 构造函数
    public CategoryResponse() {
        this.children = new ArrayList<>();
        this.childrenCount = 0;
    }

    /**
     * 从Category实体转换为CategoryResponse
     */
    public static CategoryResponse fromEntity(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setParentId(category.getParentId());
        response.setLevel(category.getLevel());
        response.setPath(category.getPath());
        response.setDescription(category.getDescription());
        response.setColor(category.getColor());
        response.setIcon(category.getIcon());
        response.setSortOrder(category.getSortOrder());
        response.setIsActive(category.getIsActive());
        response.setUserId(category.getUserId());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
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

    public List<CategoryResponse> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryResponse> children) {
        this.children = children;
        this.childrenCount = children != null ? children.size() : 0;
    }

    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }

    /**
     * 添加子分类
     */
    public void addChild(CategoryResponse child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
        this.childrenCount = this.children.size();
    }

    @Override
    public String toString() {
        return "CategoryResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", level=" + level +
                ", path='" + path + '\'' +
                ", color='" + color + '\'' +
                ", icon='" + icon + '\'' +
                ", sortOrder=" + sortOrder +
                ", childrenCount=" + childrenCount +
                '}';
    }
} 