package com.memorin.knowledge.entity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 选择题数据实体
 * 支持单选题和多选题
 */
@Entity
@Table(name = "choice_question_data", indexes = {
    @Index(name = "idx_choice_knowledge_id", columnList = "knowledge_id"),
    @Index(name = "idx_choice_type", columnList = "choice_type"),
    @Index(name = "idx_choice_points", columnList = "points")
})
public class ChoiceQuestionData {
    
    /**
     * 主键ID，格式：CQD_{date}_{seq}
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;
    
    /**
     * 关联的知识点ID
     */
    @NotBlank(message = "关联知识点ID不能为空")
    @Column(name = "knowledge_id", length = 50, nullable = false)
    private String knowledgeId;
    
    /**
     * 选择题类型：single(单选) / multiple(多选)
     */
    @NotBlank(message = "选择题类型不能为空")
    @Pattern(regexp = "^(single|multiple)$", message = "选择题类型必须是single或multiple")
    @Column(name = "choice_type", length = 20, nullable = false)
    private String choiceType;
    
    /**
     * 题目分值
     */
    @NotNull(message = "题目分值不能为空")
    @Min(value = 1, message = "题目分值不能小于1")
    @Max(value = 100, message = "题目分值不能大于100")
    @Column(name = "points", nullable = false)
    private Integer points;
    
    /**
     * 是否支持部分得分(仅多选题有效)
     */
    @Column(name = "partial_credit", nullable = false)
    private Boolean partialCredit = false;
    
    /**
     * 是否随机排序选项
     */
    @Column(name = "random_order", nullable = false)
    private Boolean randomOrder = false;
    
    /**
     * 选项列表
     */
    @Valid
    @Size(min = 2, max = 8, message = "选项数量必须在2-8个之间")
    @ElementCollection
    @CollectionTable(name = "choice_question_options", joinColumns = @JoinColumn(name = "choice_question_id"))
    @OrderBy("sortOrder ASC")
    private List<ChoiceOption> options = new ArrayList<>();
    
    /**
     * 题目解析说明
     */
    @Size(max = 1000, message = "题目解析长度不能超过1000个字符")
    @Column(name = "explanation", length = 1000)
    private String explanation;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
    
    // 构造函数
    public ChoiceQuestionData() {}
    
    public ChoiceQuestionData(String knowledgeId, String choiceType, Integer points, List<ChoiceOption> options) {
        this.knowledgeId = knowledgeId;
        this.choiceType = choiceType;
        this.points = points;
        this.options = options != null ? options : new ArrayList<>();
    }
    
    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }
    
    // 业务方法
    
    /**
     * 获取正确答案选项键列表
     */
    public List<String> getCorrectAnswerKeys() {
        return options.stream()
                .filter(ChoiceOption::getIsCorrect)
                .map(ChoiceOption::getOptionKey)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取正确答案数量
     */
    public int getCorrectAnswerCount() {
        return (int) options.stream()
                .filter(ChoiceOption::getIsCorrect)
                .count();
    }
    
    /**
     * 是否为单选题
     */
    public boolean isSingleChoice() {
        return "single".equals(this.choiceType);
    }
    
    /**
     * 是否为多选题
     */
    public boolean isMultipleChoice() {
        return "multiple".equals(this.choiceType);
    }
    
    /**
     * 验证选项配置是否正确
     */
    public boolean isValidOptionConfiguration() {
        if (options == null || options.isEmpty()) {
            return false;
        }
        
        // 检查正确答案数量
        int correctCount = getCorrectAnswerCount();
        if (isSingleChoice() && correctCount != 1) {
            return false; // 单选题必须有且仅有一个正确答案
        }
        if (isMultipleChoice() && correctCount < 1) {
            return false; // 多选题至少要有一个正确答案
        }
        
        // 检查选项键是否重复
        long uniqueKeys = options.stream()
                .map(ChoiceOption::getOptionKey)
                .distinct()
                .count();
        
        return uniqueKeys == options.size();
    }
    
    /**
     * 根据选项键获取选项
     */
    public ChoiceOption getOptionByKey(String optionKey) {
        return options.stream()
                .filter(option -> optionKey.equals(option.getOptionKey()))
                .findFirst()
                .orElse(null);
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
    
    public List<ChoiceOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<ChoiceOption> options) {
        this.options = options != null ? options : new ArrayList<>();
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
} 