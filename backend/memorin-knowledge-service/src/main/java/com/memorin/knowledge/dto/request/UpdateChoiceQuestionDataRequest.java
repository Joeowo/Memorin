package com.memorin.knowledge.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 更新选择题数据请求DTO
 */
public class UpdateChoiceQuestionDataRequest {
    
    /**
     * 选择题类型：single(单选) / multiple(多选)
     */
    @NotBlank(message = "选择题类型不能为空")
    @Pattern(regexp = "^(single|multiple)$", message = "选择题类型必须是single或multiple")
    private String choiceType;
    
    /**
     * 题目分值
     */
    @NotNull(message = "题目分值不能为空")
    @Min(value = 1, message = "题目分值不能小于1")
    @Max(value = 100, message = "题目分值不能大于100")
    private Integer points;
    
    /**
     * 是否支持部分得分(仅多选题有效)
     */
    private Boolean partialCredit = false;
    
    /**
     * 是否随机排序选项
     */
    private Boolean randomOrder = false;
    
    /**
     * 选项列表
     */
    @Valid
    @NotNull(message = "选项列表不能为空")
    @Size(min = 2, max = 8, message = "选项数量必须在2-8个之间")
    private List<ChoiceOptionRequest> options;
    
    /**
     * 题目解析说明
     */
    @Size(max = 1000, message = "题目解析长度不能超过1000个字符")
    private String explanation;
    
    // 构造函数
    public UpdateChoiceQuestionDataRequest() {}
    
    public UpdateChoiceQuestionDataRequest(String choiceType, Integer points, List<ChoiceOptionRequest> options) {
        this.choiceType = choiceType;
        this.points = points;
        this.options = options;
    }
    
    // 业务验证方法
    
    /**
     * 检查选项配置是否合理
     */
    public boolean isValidOptionConfiguration() {
        if (options == null || options.isEmpty()) {
            return false;
        }
        
        // 统计正确答案数量
        long correctCount = options.stream()
                .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                .count();
        
        // 单选题必须有且仅有一个正确答案
        if ("single".equals(choiceType) && correctCount != 1) {
            return false;
        }
        
        // 多选题至少要有一个正确答案
        if ("multiple".equals(choiceType) && correctCount < 1) {
            return false;
        }
        
        // 检查选项键是否重复
        long uniqueKeys = options.stream()
                .map(ChoiceOptionRequest::getOptionKey)
                .distinct()
                .count();
        
        return uniqueKeys == options.size();
    }
    
    /**
     * 检查部分得分配置是否合理
     */
    public boolean isValidPartialCreditConfiguration() {
        // 只有多选题才能设置部分得分
        if (Boolean.TRUE.equals(partialCredit) && !"multiple".equals(choiceType)) {
            return false;
        }
        return true;
    }
    
    /**
     * 检查选项排序是否连续
     */
    public boolean isValidSortOrderConfiguration() {
        if (options == null || options.isEmpty()) {
            return false;
        }
        
        // 检查排序顺序是否从0开始且连续
        List<Integer> sortOrders = options.stream()
                .map(ChoiceOptionRequest::getSortOrder)
                .sorted()
                .collect(java.util.stream.Collectors.toList());
        
        for (int i = 0; i < sortOrders.size(); i++) {
            if (sortOrders.get(i) != i) {
                return false;
            }
        }
        
        return true;
    }
    
    // Getter和Setter方法
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
    
    public List<ChoiceOptionRequest> getOptions() {
        return options;
    }
    
    public void setOptions(List<ChoiceOptionRequest> options) {
        this.options = options;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
} 