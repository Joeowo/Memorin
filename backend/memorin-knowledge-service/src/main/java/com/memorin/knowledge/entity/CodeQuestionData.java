package com.memorin.knowledge.entity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 编程题数据实体（简化版）
 * 专注于核心编程题功能
 */
@Entity
@Table(name = "code_question_data", indexes = {
    @Index(name = "idx_code_knowledge_id", columnList = "knowledge_id"),
    @Index(name = "idx_code_language", columnList = "programming_language"),
    @Index(name = "idx_code_difficulty", columnList = "difficulty_level"),
    @Index(name = "idx_code_points", columnList = "points")
})
public class CodeQuestionData {
    
    /**
     * 编程题数据ID
     * 格式：CQDC_{date}_{seq} (CQD Code)
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
     * 编程语言
     */
    @NotBlank(message = "编程语言不能为空")
    @Pattern(regexp = "^(java|python|javascript|cpp|c|sql|html|css)$", 
             message = "编程语言必须是java、python、javascript、cpp、c、sql、html、css之一")
    @Column(name = "programming_language", length = 20, nullable = false)
    private String programmingLanguage;
    
    /**
     * 题目标题
     */
    @NotBlank(message = "题目标题不能为空")
    @Size(max = 200, message = "题目标题长度不能超过200个字符")
    @Column(name = "title", length = 200, nullable = false)
    private String title;
    
    /**
     * 题目描述
     */
    @NotBlank(message = "题目描述不能为空")
    @Size(max = 5000, message = "题目描述长度不能超过5000个字符")
    @Column(name = "description", length = 5000, nullable = false)
    private String description;
    
    /**
     * 难度等级 (1-5)
     */
    @NotNull(message = "难度等级不能为空")
    @Min(value = 1, message = "难度等级不能小于1")
    @Max(value = 5, message = "难度等级不能大于5")
    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel;
    
    /**
     * 题目总分值
     */
    @NotNull(message = "题目分值不能为空")
    @Min(value = 1, message = "题目分值不能小于1")
    @Max(value = 100, message = "题目分值不能大于100")
    @Column(name = "points", nullable = false)
    private Integer points;
    
    /**
     * 初始代码模板（可选）
     */
    @Size(max = 3000, message = "初始代码模板长度不能超过3000个字符")
    @Column(name = "initial_code", length = 3000)
    private String initialCode;
    
    /**
     * 解题提示（可选）
     */
    @Size(max = 1000, message = "解题提示长度不能超过1000个字符")
    @Column(name = "hints", length = 1000)
    private String hints;
    
    /**
     * 测试用例集合
     */
    @Valid
    @Size(min = 1, max = 20, message = "测试用例数量必须在1-20个之间")
    @ElementCollection
    @CollectionTable(name = "code_question_test_cases", 
                    joinColumns = @JoinColumn(name = "code_question_id"))
    @OrderBy("sortOrder ASC")
    private List<TestCase> testCases = new ArrayList<>();
    
    /**
     * 标准答案代码（可选，用于参考）
     */
    @Size(max = 5000, message = "标准答案代码长度不能超过5000个字符")
    @Column(name = "standard_answer", length = 5000)
    private String standardAnswer;
    
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
    
    // 构造方法
    public CodeQuestionData() {
    }
    
    public CodeQuestionData(String knowledgeId, String programmingLanguage, String title, 
                           String description, Integer difficultyLevel, Integer points) {
        this.knowledgeId = knowledgeId;
        this.programmingLanguage = programmingLanguage;
        this.title = title;
        this.description = description;
        this.difficultyLevel = difficultyLevel;
        this.points = points;
    }
    
    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }
    
    // 业务方法
    
    /**
     * 计算所有测试用例的总分值
     */
    public int getTotalTestCasePoints() {
        return testCases.stream()
                .mapToInt(TestCase::getCasePoints)
                .sum();
    }
    
    /**
     * 获取公开测试用例数量
     */
    public long getPublicTestCaseCount() {
        return testCases.stream()
                .filter(testCase -> !testCase.getIsHidden())
                .count();
    }
    
    /**
     * 获取隐藏测试用例数量
     */
    public long getHiddenTestCaseCount() {
        return testCases.stream()
                .filter(TestCase::getIsHidden)
                .count();
    }
    
    /**
     * 验证测试用例分值总和是否等于题目分值
     */
    public boolean isTestCasePointsValid() {
        return getTotalTestCasePoints() == this.points;
    }
    
    /**
     * 检查是否有初始代码模板
     */
    public boolean hasInitialCode() {
        return initialCode != null && !initialCode.trim().isEmpty();
    }
    
    /**
     * 检查是否有解题提示
     */
    public boolean hasHints() {
        return hints != null && !hints.trim().isEmpty();
    }
    
    /**
     * 检查是否有标准答案
     */
    public boolean hasStandardAnswer() {
        return standardAnswer != null && !standardAnswer.trim().isEmpty();
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
    
    public String getProgrammingLanguage() {
        return programmingLanguage;
    }
    
    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public String getInitialCode() {
        return initialCode;
    }
    
    public void setInitialCode(String initialCode) {
        this.initialCode = initialCode;
    }
    
    public String getHints() {
        return hints;
    }
    
    public void setHints(String hints) {
        this.hints = hints;
    }
    
    public List<TestCase> getTestCases() {
        return testCases;
    }
    
    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
    
    public String getStandardAnswer() {
        return standardAnswer;
    }
    
    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
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
        return "CodeQuestionData{" +
                "id='" + id + '\'' +
                ", knowledgeId='" + knowledgeId + '\'' +
                ", programmingLanguage='" + programmingLanguage + '\'' +
                ", title='" + title + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                ", points=" + points +
                ", testCaseCount=" + (testCases != null ? testCases.size() : 0) +
                ", createdAt=" + createdAt +
                '}';
    }
} 