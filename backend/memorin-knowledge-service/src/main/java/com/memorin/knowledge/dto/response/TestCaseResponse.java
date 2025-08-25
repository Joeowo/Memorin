package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.TestCase;

/**
 * 测试用例响应DTO
 * 用于编程题测试用例的数据响应
 */
public class TestCaseResponse {
    
    /**
     * 测试用例名称
     */
    private String caseName;
    
    /**
     * 输入数据
     */
    private String inputData;
    
    /**
     * 期望输出
     */
    private String expectedOutput;
    
    /**
     * 是否为隐藏测试用例
     */
    private Boolean isHidden;
    
    /**
     * 测试用例分值
     */
    private Integer casePoints;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 用例描述
     */
    private String description;
    
    // 构造方法
    public TestCaseResponse() {
    }
    
    /**
     * 从测试用例实体转换为响应DTO
     */
    public static TestCaseResponse fromEntity(TestCase testCase) {
        if (testCase == null) {
            return null;
        }
        
        TestCaseResponse response = new TestCaseResponse();
        response.setCaseName(testCase.getCaseName());
        response.setInputData(testCase.getInputData());
        response.setExpectedOutput(testCase.getExpectedOutput());
        response.setIsHidden(testCase.getIsHidden());
        response.setCasePoints(testCase.getCasePoints());
        response.setSortOrder(testCase.getSortOrder());
        response.setDescription(testCase.getDescription());
        
        return response;
    }
    
    // Getter和Setter方法
    public String getCaseName() {
        return caseName;
    }
    
    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }
    
    public String getInputData() {
        return inputData;
    }
    
    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
    
    public String getExpectedOutput() {
        return expectedOutput;
    }
    
    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
    
    public Boolean getIsHidden() {
        return isHidden;
    }
    
    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
    
    public Integer getCasePoints() {
        return casePoints;
    }
    
    public void setCasePoints(Integer casePoints) {
        this.casePoints = casePoints;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "TestCaseResponse{" +
                "caseName='" + caseName + '\'' +
                ", inputData='" + inputData + '\'' +
                ", expectedOutput='" + expectedOutput + '\'' +
                ", isHidden=" + isHidden +
                ", casePoints=" + casePoints +
                ", sortOrder=" + sortOrder +
                ", description='" + description + '\'' +
                '}';
    }
} 