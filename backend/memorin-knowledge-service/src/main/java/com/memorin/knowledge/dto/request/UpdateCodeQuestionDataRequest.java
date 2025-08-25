package com.memorin.knowledge.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 更新编程题数据请求DTO
 */
public class UpdateCodeQuestionDataRequest {
    
    /**
     * 编程语言
     */
    @NotBlank(message = "编程语言不能为空")
    @Pattern(regexp = "^(java|python|javascript|cpp|c|sql|html|css)$", 
             message = "编程语言必须是java、python、javascript、cpp、c、sql、html、css之一")
    private String programmingLanguage;
    
    /**
     * 题目标题
     */
    @NotBlank(message = "题目标题不能为空")
    @Size(max = 200, message = "题目标题长度不能超过200个字符")
    private String title;
    
    /**
     * 题目描述
     */
    @NotBlank(message = "题目描述不能为空")
    @Size(max = 5000, message = "题目描述长度不能超过5000个字符")
    private String description;
    
    /**
     * 难度等级 (1-5)
     */
    @NotNull(message = "难度等级不能为空")
    @Min(value = 1, message = "难度等级不能小于1")
    @Max(value = 5, message = "难度等级不能大于5")
    private Integer difficultyLevel;
    
    /**
     * 题目总分值
     */
    @NotNull(message = "题目分值不能为空")
    @Min(value = 1, message = "题目分值不能小于1")
    @Max(value = 100, message = "题目分值不能大于100")
    private Integer points;
    
    /**
     * 初始代码模板（可选）
     */
    @Size(max = 3000, message = "初始代码模板长度不能超过3000个字符")
    private String initialCode;
    
    /**
     * 解题提示（可选）
     */
    @Size(max = 1000, message = "解题提示长度不能超过1000个字符")
    private String hints;
    
    /**
     * 测试用例集合
     */
    @Valid
    @NotNull(message = "测试用例集合不能为空")
    @Size(min = 1, max = 20, message = "测试用例数量必须在1-20个之间")
    private List<TestCaseRequest> testCases;
    
    /**
     * 标准答案代码（可选）
     */
    @Size(max = 5000, message = "标准答案代码长度不能超过5000个字符")
    private String standardAnswer;
    
    // 构造方法
    public UpdateCodeQuestionDataRequest() {
    }
    
    // 业务验证方法
    
    /**
     * 验证测试用例配置是否合理
     */
    public boolean isValidTestCaseConfiguration() {
        if (testCases == null || testCases.isEmpty()) {
            return false;
        }
        
        // 检查测试用例名称是否重复
        Set<String> caseNames = testCases.stream()
                .map(TestCaseRequest::getCaseName)
                .collect(Collectors.toSet());
        if (caseNames.size() != testCases.size()) {
            return false;
        }
        
        // 检查排序顺序是否从0开始且连续
        List<Integer> sortOrders = testCases.stream()
                .map(TestCaseRequest::getSortOrder)
                .sorted()
                .collect(Collectors.toList());
        
        for (int i = 0; i < sortOrders.size(); i++) {
            if (sortOrders.get(i) != i) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 验证测试用例分值总和是否等于题目总分值
     */
    public boolean isValidTestCasePointsConfiguration() {
        if (testCases == null || testCases.isEmpty() || points == null) {
            return false;
        }
        
        int totalTestCasePoints = testCases.stream()
                .mapToInt(TestCaseRequest::getCasePoints)
                .sum();
        
        return totalTestCasePoints == points;
    }
    
    /**
     * 检查是否至少有一个公开测试用例
     */
    public boolean hasPublicTestCase() {
        if (testCases == null || testCases.isEmpty()) {
            return false;
        }
        
        return testCases.stream()
                .anyMatch(testCase -> !testCase.getIsHidden());
    }
    
    /**
     * 检查测试用例分值是否都大于0
     */
    public boolean isValidTestCasePoints() {
        if (testCases == null || testCases.isEmpty()) {
            return false;
        }
        
        return testCases.stream()
                .allMatch(testCase -> testCase.getCasePoints() != null && testCase.getCasePoints() > 0);
    }
    
    /**
     * 获取公开测试用例数量
     */
    public long getPublicTestCaseCount() {
        if (testCases == null || testCases.isEmpty()) {
            return 0;
        }
        
        return testCases.stream()
                .filter(testCase -> !testCase.getIsHidden())
                .count();
    }
    
    /**
     * 获取隐藏测试用例数量
     */
    public long getHiddenTestCaseCount() {
        if (testCases == null || testCases.isEmpty()) {
            return 0;
        }
        
        return testCases.stream()
                .filter(TestCaseRequest::getIsHidden)
                .count();
    }
    
    /**
     * 检查排序顺序连续性（更新时专用）
     */
    public boolean isValidSortOrderConfiguration() {
        if (testCases == null || testCases.isEmpty()) {
            return false;
        }
        
        // 检查排序顺序是否从0开始且连续
        List<Integer> sortOrders = testCases.stream()
                .map(TestCaseRequest::getSortOrder)
                .sorted()
                .collect(Collectors.toList());
        
        for (int i = 0; i < sortOrders.size(); i++) {
            if (sortOrders.get(i) != i) {
                return false;
            }
        }
        
        return true;
    }
    
    // Getter和Setter方法
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
    
    public List<TestCaseRequest> getTestCases() {
        return testCases;
    }
    
    public void setTestCases(List<TestCaseRequest> testCases) {
        this.testCases = testCases;
    }
    
    public String getStandardAnswer() {
        return standardAnswer;
    }
    
    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }
    
    @Override
    public String toString() {
        return "UpdateCodeQuestionDataRequest{" +
                "programmingLanguage='" + programmingLanguage + '\'' +
                ", title='" + title + '\'' +
                ", difficultyLevel=" + difficultyLevel +
                ", points=" + points +
                ", testCaseCount=" + (testCases != null ? testCases.size() : 0) +
                ", hasInitialCode=" + (initialCode != null && !initialCode.trim().isEmpty()) +
                ", hasHints=" + (hints != null && !hints.trim().isEmpty()) +
                ", hasStandardAnswer=" + (standardAnswer != null && !standardAnswer.trim().isEmpty()) +
                '}';
    }
} 