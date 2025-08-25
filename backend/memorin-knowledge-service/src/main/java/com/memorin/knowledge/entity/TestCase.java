package com.memorin.knowledge.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 测试用例嵌套实体
 * 用于代码题的输入输出测试用例
 */
@Embeddable
public class TestCase {
    
    /**
     * 测试用例名称
     */
    @NotBlank(message = "测试用例名称不能为空")
    @Size(max = 100, message = "测试用例名称长度不能超过100个字符")
    @Column(name = "case_name", length = 100, nullable = false)
    private String caseName;
    
    /**
     * 输入数据
     */
    @NotBlank(message = "输入数据不能为空")
    @Size(max = 2000, message = "输入数据长度不能超过2000个字符")
    @Column(name = "input_data", length = 2000, nullable = false)
    private String inputData;
    
    /**
     * 期望输出
     */
    @NotBlank(message = "期望输出不能为空")
    @Size(max = 2000, message = "期望输出长度不能超过2000个字符")
    @Column(name = "expected_output", length = 2000, nullable = false)
    private String expectedOutput;
    
    /**
     * 是否为隐藏测试用例
     */
    @NotNull(message = "是否为隐藏测试用例不能为空")
    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;
    
    /**
     * 测试用例分值
     */
    @NotNull(message = "测试用例分值不能为空")
    @Column(name = "case_points", nullable = false)
    private Integer casePoints;
    
    /**
     * 排序顺序
     */
    @NotNull(message = "排序顺序不能为空")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
    
    /**
     * 用例描述
     */
    @Size(max = 500, message = "用例描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;
    
    // 构造方法
    public TestCase() {
    }
    
    public TestCase(String caseName, String inputData, String expectedOutput, 
                   Boolean isHidden, Integer casePoints, Integer sortOrder, String description) {
        this.caseName = caseName;
        this.inputData = inputData;
        this.expectedOutput = expectedOutput;
        this.isHidden = isHidden;
        this.casePoints = casePoints;
        this.sortOrder = sortOrder;
        this.description = description;
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
        return "TestCase{" +
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