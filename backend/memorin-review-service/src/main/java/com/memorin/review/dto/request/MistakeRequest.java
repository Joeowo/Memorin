package com.memorin.review.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 错题操作请求DTO
 */
public class MistakeRequest {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    @Size(max = 50, message = "用户ID长度不能超过50个字符")
    private String userId;

    /**
     * 知识点ID
     */
    @NotBlank(message = "知识点ID不能为空")
    @Size(max = 50, message = "知识点ID长度不能超过50个字符")
    private String knowledgePointId;

    /**
     * 错误原因
     */
    @Size(max = 500, message = "错误原因长度不能超过500个字符")
    private String mistakeReason;

    /**
     * 题目类型
     */
    @Size(max = 20, message = "题目类型长度不能超过20个字符")
    private String questionType;

    /**
     * 难度等级
     */
    @Size(max = 20, message = "难度等级长度不能超过20个字符")
    private String difficultyLevel;

    /**
     * 错误标签
     */
    @Size(max = 500, message = "错误标签长度不能超过500个字符")
    private String mistakeTags;

    /**
     * 用户备注
     */
    @Size(max = 1000, message = "用户备注长度不能超过1000个字符")
    private String userNotes;

    // 构造函数
    public MistakeRequest() {
    }

    public MistakeRequest(String userId, String knowledgePointId, String mistakeReason) {
        this.userId = userId;
        this.knowledgePointId = knowledgePointId;
        this.mistakeReason = mistakeReason;
    }

    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getMistakeReason() {
        return mistakeReason;
    }

    public void setMistakeReason(String mistakeReason) {
        this.mistakeReason = mistakeReason;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getMistakeTags() {
        return mistakeTags;
    }

    public void setMistakeTags(String mistakeTags) {
        this.mistakeTags = mistakeTags;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

    @Override
    public String toString() {
        return "MistakeRequest{" +
                "userId='" + userId + '\'' +
                ", knowledgePointId='" + knowledgePointId + '\'' +
                ", mistakeReason='" + mistakeReason + '\'' +
                ", questionType='" + questionType + '\'' +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                ", mistakeTags='" + mistakeTags + '\'' +
                ", userNotes='" + userNotes + '\'' +
                '}';
    }
} 