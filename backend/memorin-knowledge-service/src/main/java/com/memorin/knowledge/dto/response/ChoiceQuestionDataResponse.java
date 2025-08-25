package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.ChoiceQuestionData;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 选择题数据响应DTO
 */
public class ChoiceQuestionDataResponse {
    
    /**
     * 主键ID
     */
    private String id;
    
    /**
     * 关联的知识点ID
     */
    private String knowledgeId;
    
    /**
     * 选择题类型：single(单选) / multiple(多选)
     */
    private String choiceType;
    
    /**
     * 题目分值
     */
    private Integer points;
    
    /**
     * 是否支持部分得分(仅多选题有效)
     */
    private Boolean partialCredit;
    
    /**
     * 是否随机排序选项
     */
    private Boolean randomOrder;
    
    /**
     * 选项列表
     */
    private List<ChoiceOptionResponse> options;
    
    /**
     * 题目解析说明
     */
    private String explanation;
    
    /**
     * 创建时间
     */
    private Long createdAt;
    
    /**
     * 更新时间
     */
    private Long updatedAt;
    
    // 计算属性
    
    /**
     * 正确答案选项键列表
     */
    private List<String> correctAnswerKeys;
    
    /**
     * 正确答案数量
     */
    private Integer correctAnswerCount;
    
    /**
     * 是否为单选题
     */
    private Boolean isSingleChoice;
    
    /**
     * 是否为多选题
     */
    private Boolean isMultipleChoice;
    
    /**
     * 选项总数
     */
    private Integer totalOptions;
    
    // 构造函数
    public ChoiceQuestionDataResponse() {}
    
    /**
     * 从ChoiceQuestionData实体转换为响应DTO
     */
    public static ChoiceQuestionDataResponse fromEntity(ChoiceQuestionData choiceData) {
        ChoiceQuestionDataResponse response = new ChoiceQuestionDataResponse();
        
        // 基础字段
        response.setId(choiceData.getId());
        response.setKnowledgeId(choiceData.getKnowledgeId());
        response.setChoiceType(choiceData.getChoiceType());
        response.setPoints(choiceData.getPoints());
        response.setPartialCredit(choiceData.getPartialCredit());
        response.setRandomOrder(choiceData.getRandomOrder());
        response.setExplanation(choiceData.getExplanation());
        response.setCreatedAt(choiceData.getCreatedAt());
        response.setUpdatedAt(choiceData.getUpdatedAt());
        
        // 选项列表转换
        if (choiceData.getOptions() != null) {
            response.setOptions(
                choiceData.getOptions().stream()
                    .map(ChoiceOptionResponse::fromEntity)
                    .collect(Collectors.toList())
            );
        }
        
        // 计算属性
        response.setCorrectAnswerKeys(choiceData.getCorrectAnswerKeys());
        response.setCorrectAnswerCount(choiceData.getCorrectAnswerCount());
        response.setIsSingleChoice(choiceData.isSingleChoice());
        response.setIsMultipleChoice(choiceData.isMultipleChoice());
        response.setTotalOptions(choiceData.getOptions() != null ? choiceData.getOptions().size() : 0);
        
        return response;
    }
    
    // 辅助方法
    
    /**
     * 获取指定选项键的选项
     */
    public ChoiceOptionResponse getOptionByKey(String optionKey) {
        if (options == null) {
            return null;
        }
        return options.stream()
                .filter(option -> optionKey.equals(option.getOptionKey()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查答案是否正确
     */
    public boolean isCorrectAnswer(List<String> userAnswers) {
        if (userAnswers == null || correctAnswerKeys == null) {
            return false;
        }
        
        if (isSingleChoice) {
            // 单选题：用户答案必须是唯一的正确答案
            return userAnswers.size() == 1 && correctAnswerKeys.contains(userAnswers.get(0));
        } else {
            // 多选题：用户答案必须完全匹配所有正确答案
            return userAnswers.size() == correctAnswerKeys.size() && 
                   correctAnswerKeys.containsAll(userAnswers);
        }
    }
    
    /**
     * 计算部分得分（仅多选题）
     */
    public double calculatePartialScore(List<String> userAnswers) {
        if (!isMultipleChoice || !partialCredit || userAnswers == null || correctAnswerKeys == null) {
            return isCorrectAnswer(userAnswers) ? points : 0;
        }
        
        // 计算正确选择的数量
        long correctSelections = userAnswers.stream()
                .filter(correctAnswerKeys::contains)
                .count();
        
        // 计算错误选择的数量
        long incorrectSelections = userAnswers.stream()
                .filter(answer -> !correctAnswerKeys.contains(answer))
                .count();
        
        // 部分得分公式：(正确数量 - 错误数量) / 总正确数量 * 总分
        double ratio = Math.max(0, (double)(correctSelections - incorrectSelections) / correctAnswerKeys.size());
        return ratio * points;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getKnowledgeId() {
        return knowledgeId;
    }
    
    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
    }
    
    public String getChoiceType() {
        return choiceType;
    }
    
    public void setChoiceType(String choiceType) {
        this.choiceType = choiceType;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public Boolean getPartialCredit() {
        return partialCredit;
    }
    
    public void setPartialCredit(Boolean partialCredit) {
        this.partialCredit = partialCredit;
    }
    
    public Boolean getRandomOrder() {
        return randomOrder;
    }
    
    public void setRandomOrder(Boolean randomOrder) {
        this.randomOrder = randomOrder;
    }
    
    public List<ChoiceOptionResponse> getOptions() {
        return options;
    }
    
    public void setOptions(List<ChoiceOptionResponse> options) {
        this.options = options;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
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
    
    public List<String> getCorrectAnswerKeys() {
        return correctAnswerKeys;
    }
    
    public void setCorrectAnswerKeys(List<String> correctAnswerKeys) {
        this.correctAnswerKeys = correctAnswerKeys;
    }
    
    public Integer getCorrectAnswerCount() {
        return correctAnswerCount;
    }
    
    public void setCorrectAnswerCount(Integer correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
    }
    
    public Boolean getIsSingleChoice() {
        return isSingleChoice;
    }
    
    public void setIsSingleChoice(Boolean isSingleChoice) {
        this.isSingleChoice = isSingleChoice;
    }
    
    public Boolean getIsMultipleChoice() {
        return isMultipleChoice;
    }
    
    public void setIsMultipleChoice(Boolean isMultipleChoice) {
        this.isMultipleChoice = isMultipleChoice;
    }
    
    public Integer getTotalOptions() {
        return totalOptions;
    }
    
    public void setTotalOptions(Integer totalOptions) {
        this.totalOptions = totalOptions;
    }
} 