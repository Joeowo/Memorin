package com.memorin.knowledge.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 选择题选项嵌入实体
 * 用于ChoiceQuestionData中的选项管理
 */
@Embeddable
public class ChoiceOption {
    
    /**
     * 选项标识 (A, B, C, D等)
     */
    @NotBlank(message = "选项标识不能为空")
    @Pattern(regexp = "^[A-Z]$", message = "选项标识必须是A-Z中的单个大写字母")
    @Column(name = "option_key", length = 1, nullable = false)
    private String optionKey;
    
    /**
     * 选项文本内容
     */
    @NotBlank(message = "选项内容不能为空")
    @Size(max = 1000, message = "选项内容长度不能超过1000个字符")
    @Column(name = "option_text", length = 1000, nullable = false)
    private String optionText;
    
    /**
     * 是否为正确答案
     */
    @NotNull(message = "是否正确不能为空")
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
    
    /**
     * 选项解释说明
     */
    @Size(max = 500, message = "选项解释长度不能超过500个字符")
    @Column(name = "explanation", length = 500)
    private String explanation;
    
    /**
     * 选项排序顺序
     */
    @NotNull(message = "排序顺序不能为空")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
    
    // 构造函数
    public ChoiceOption() {}
    
    public ChoiceOption(String optionKey, String optionText, Boolean isCorrect, String explanation, Integer sortOrder) {
        this.optionKey = optionKey;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.explanation = explanation;
        this.sortOrder = sortOrder;
    }
    
    // Getter和Setter方法
    public String getOptionKey() {
        return optionKey;
    }
    
    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }
    
    public String getOptionText() {
        return optionText;
    }
    
    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
    
    public Boolean getIsCorrect() {
        return isCorrect;
    }
    
    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
} 