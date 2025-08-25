package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.CodeQuestionData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 编程题数据响应DTO
 */
public class CodeQuestionDataResponse {
    
    /**
     * 编程题数据ID
     */
    private String id;
    
    /**
     * 关联的知识点ID
     */
    private String knowledgeId;
    
    /**
     * 编程语言
     */
    private String programmingLanguage;
    
    /**
     * 题目标题
     */
    private String title;
    
    /**
     * 题目描述
     */
    private String description;
    
    /**
     * 难度等级 (1-5)
     */
    private Integer difficultyLevel;
    
    /**
     * 题目总分值
     */
    private Integer points;
    
    /**
     * 初始代码模板（可选）
     */
    private String initialCode;
    
    /**
     * 解题提示（可选）
     */
    private String hints;
    
    /**
     * 测试用例集合
     */
    private List<TestCaseResponse> testCases;
    
    /**
     * 标准答案代码（可选）
     */
    private String standardAnswer;
    
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
     * 测试用例总分值
     */
    private Integer totalTestCasePoints;
    
    /**
     * 公开测试用例数量
     */
    private Long publicTestCaseCount;
    
    /**
     * 隐藏测试用例数量
     */
    private Long hiddenTestCaseCount;
    
    /**
     * 测试用例总数量
     */
    private Integer totalTestCases;
    
    /**
     * 是否有初始代码模板
     */
    private Boolean hasInitialCode;
    
    /**
     * 是否有解题提示
     */
    private Boolean hasHints;
    
    /**
     * 是否有标准答案
     */
    private Boolean hasStandardAnswer;
    
    /**
     * 测试用例分值是否有效（总和等于题目分值）
     */
    private Boolean isTestCasePointsValid;
    
    // 构造方法
    public CodeQuestionDataResponse() {
    }
    
    /**
     * 从编程题实体转换为响应DTO
     */
    public static CodeQuestionDataResponse fromEntity(CodeQuestionData codeQuestionData) {
        if (codeQuestionData == null) {
            return null;
        }
        
        CodeQuestionDataResponse response = new CodeQuestionDataResponse();
        
        // 基础字段
        response.setId(codeQuestionData.getId());
        response.setKnowledgeId(codeQuestionData.getKnowledgeId());
        response.setProgrammingLanguage(codeQuestionData.getProgrammingLanguage());
        response.setTitle(codeQuestionData.getTitle());
        response.setDescription(codeQuestionData.getDescription());
        response.setDifficultyLevel(codeQuestionData.getDifficultyLevel());
        response.setPoints(codeQuestionData.getPoints());
        response.setInitialCode(codeQuestionData.getInitialCode());
        response.setHints(codeQuestionData.getHints());
        response.setStandardAnswer(codeQuestionData.getStandardAnswer());
        response.setCreatedAt(codeQuestionData.getCreatedAt());
        response.setUpdatedAt(codeQuestionData.getUpdatedAt());
        
        // 转换测试用例集合
        if (codeQuestionData.getTestCases() != null) {
            List<TestCaseResponse> testCaseResponses = codeQuestionData.getTestCases().stream()
                    .map(TestCaseResponse::fromEntity)
                    .collect(Collectors.toList());
            response.setTestCases(testCaseResponses);
        }
        
        // 计算属性
        response.setTotalTestCasePoints(codeQuestionData.getTotalTestCasePoints());
        response.setPublicTestCaseCount(codeQuestionData.getPublicTestCaseCount());
        response.setHiddenTestCaseCount(codeQuestionData.getHiddenTestCaseCount());
        response.setTotalTestCases(codeQuestionData.getTestCases() != null ? codeQuestionData.getTestCases().size() : 0);
        response.setHasInitialCode(codeQuestionData.hasInitialCode());
        response.setHasHints(codeQuestionData.hasHints());
        response.setHasStandardAnswer(codeQuestionData.hasStandardAnswer());
        response.setIsTestCasePointsValid(codeQuestionData.isTestCasePointsValid());
        
        return response;
    }
    
    // 辅助方法
    
    /**
     * 获取难度等级描述
     */
    public String getDifficultyLevelDescription() {
        if (difficultyLevel == null) {
            return "未知";
        }
        
        switch (difficultyLevel) {
            case 1: return "简单";
            case 2: return "较简单";
            case 3: return "中等";
            case 4: return "较困难";
            case 5: return "困难";
            default: return "未知";
        }
    }
    
    /**
     * 获取编程语言显示名称
     */
    public String getProgrammingLanguageDisplayName() {
        if (programmingLanguage == null) {
            return "未知";
        }
        
        switch (programmingLanguage.toLowerCase()) {
            case "java": return "Java";
            case "python": return "Python";
            case "javascript": return "JavaScript";
            case "cpp": return "C++";
            case "c": return "C语言";
            case "sql": return "SQL";
            case "html": return "HTML";
            case "css": return "CSS";
            default: return programmingLanguage.toUpperCase();
        }
    }
    
    /**
     * 检查是否为简单题目（难度1-2级且分值低于30分）
     */
    public Boolean isSimpleProblem() {
        return difficultyLevel != null && difficultyLevel <= 2 && 
               points != null && points < 30;
    }
    
    /**
     * 检查是否为复杂题目（难度4-5级或分值高于70分）
     */
    public Boolean isComplexProblem() {
        return (difficultyLevel != null && difficultyLevel >= 4) || 
               (points != null && points > 70);
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
    
    public List<TestCaseResponse> getTestCases() {
        return testCases;
    }
    
    public void setTestCases(List<TestCaseResponse> testCases) {
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
    
    public Integer getTotalTestCasePoints() {
        return totalTestCasePoints;
    }
    
    public void setTotalTestCasePoints(Integer totalTestCasePoints) {
        this.totalTestCasePoints = totalTestCasePoints;
    }
    
    public Long getPublicTestCaseCount() {
        return publicTestCaseCount;
    }
    
    public void setPublicTestCaseCount(Long publicTestCaseCount) {
        this.publicTestCaseCount = publicTestCaseCount;
    }
    
    public Long getHiddenTestCaseCount() {
        return hiddenTestCaseCount;
    }
    
    public void setHiddenTestCaseCount(Long hiddenTestCaseCount) {
        this.hiddenTestCaseCount = hiddenTestCaseCount;
    }
    
    public Integer getTotalTestCases() {
        return totalTestCases;
    }
    
    public void setTotalTestCases(Integer totalTestCases) {
        this.totalTestCases = totalTestCases;
    }
    
    public Boolean getHasInitialCode() {
        return hasInitialCode;
    }
    
    public void setHasInitialCode(Boolean hasInitialCode) {
        this.hasInitialCode = hasInitialCode;
    }
    
    public Boolean getHasHints() {
        return hasHints;
    }
    
    public void setHasHints(Boolean hasHints) {
        this.hasHints = hasHints;
    }
    
    public Boolean getHasStandardAnswer() {
        return hasStandardAnswer;
    }
    
    public void setHasStandardAnswer(Boolean hasStandardAnswer) {
        this.hasStandardAnswer = hasStandardAnswer;
    }
    
    public Boolean getIsTestCasePointsValid() {
        return isTestCasePointsValid;
    }
    
    public void setIsTestCasePointsValid(Boolean isTestCasePointsValid) {
        this.isTestCasePointsValid = isTestCasePointsValid;
    }
    
    @Override
    public String toString() {
        return "CodeQuestionDataResponse{" +
                "id='" + id + '\'' +
                ", knowledgeId='" + knowledgeId + '\'' +
                ", programmingLanguage='" + programmingLanguage + '\'' +
                ", title='" + title + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                ", points=" + points +
                ", totalTestCases=" + totalTestCases +
                ", publicTestCaseCount=" + publicTestCaseCount +
                ", hiddenTestCaseCount=" + hiddenTestCaseCount +
                ", hasInitialCode=" + hasInitialCode +
                ", hasHints=" + hasHints +
                ", hasStandardAnswer=" + hasStandardAnswer +
                ", isTestCasePointsValid=" + isTestCasePointsValid +
                ", createdAt=" + createdAt +
                '}';
    }
} 