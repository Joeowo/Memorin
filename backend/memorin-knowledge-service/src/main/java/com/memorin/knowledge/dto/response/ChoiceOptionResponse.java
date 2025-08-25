package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.ChoiceOption;

/**
 * 选择题选项响应DTO
 */
public class ChoiceOptionResponse {
    
    /**
     * 选项标识 (A, B, C, D等)
     */
    private String optionKey;
    
    /**
     * 选项文本内容
     */
    private String optionText;
    
    /**
     * 是否为正确答案
     */
    private Boolean isCorrect;
    
    /**
     * 选项解释说明
     */
    private String explanation;
    
    /**
     * 选项排序顺序
     */
    private Integer sortOrder;
    
    // 构造函数
    public ChoiceOptionResponse() {}
    
    public ChoiceOptionResponse(String optionKey, String optionText, Boolean isCorrect, String explanation, Integer sortOrder) {
        this.optionKey = optionKey;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.explanation = explanation;
        this.sortOrder = sortOrder;
    }
    
    /**
     * 从ChoiceOption实体转换为响应DTO
     */
    public static ChoiceOptionResponse fromEntity(ChoiceOption choiceOption) {
        ChoiceOptionResponse response = new ChoiceOptionResponse();
        response.setOptionKey(choiceOption.getOptionKey());
        response.setOptionText(choiceOption.getOptionText());
        response.setIsCorrect(choiceOption.getIsCorrect());
        response.setExplanation(choiceOption.getExplanation());
        response.setSortOrder(choiceOption.getSortOrder());
        return response;
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