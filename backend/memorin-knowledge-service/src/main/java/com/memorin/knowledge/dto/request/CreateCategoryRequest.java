package com.memorin.knowledge.dto.request;

import javax.validation.constraints.*;

/**
 * 创建分类请求DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class CreateCategoryRequest {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    /**
     * 父分类ID（可选，为空则创建顶级分类）
     */
    @Size(max = 50, message = "父分类ID长度不能超过50个字符")
    private String parentId;

    /**
     * 分类描述（可选）
     */
    @Size(max = 1000, message = "分类描述长度不能超过1000个字符")
    private String description;

    /**
     * 显示颜色（hex格式）
     */
    @NotBlank(message = "显示颜色不能为空")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式必须为hex格式，如#667eea")
    private String color;

    /**
     * 显示图标
     */
    @NotBlank(message = "显示图标不能为空")
    @Size(max = 50, message = "图标长度不能超过50个字符")
    private String icon;

    /**
     * 排序权重（可选，默认为0）
     */
    @Min(value = 0, message = "排序权重不能为负数")
    private Integer sortOrder = 0;

    // 构造函数
    public CreateCategoryRequest() {}

    // Getter和Setter方法
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

    @Override
    public String toString() {
        return "CreateCategoryRequest{" +
                "name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", description='" + description + '\'' +
                ", color='" + color + '\'' +
                ", icon='" + icon + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
} 