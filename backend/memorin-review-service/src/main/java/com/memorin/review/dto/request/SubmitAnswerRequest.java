package com.memorin.review.dto.request;

import javax.validation.constraints.*;

/**
 * 提交答案请求DTO
 * 用于接收复习过程中的答案提交信息
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class SubmitAnswerRequest {

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 知识点ID
     */
    @NotBlank(message = "知识点ID不能为空")
    private String knowledgePointId;

    /**
     * 题目在会话中的索引
     */
    @NotNull(message = "题目索引不能为空")
    @Min(value = 0, message = "题目索引不能小于0")
    private Integer questionIndex;

    /**
     * 用户答案JSON
     * 存储用户的具体答案内容
     */
    @Size(max = 3000, message = "用户答案长度不能超过3000个字符")
    private String userAnswer;

    /**
     * 是否答对
     */
    private Boolean isCorrect;

    /**
     * 质量评分 (1-3)
     * 1=错误/困难, 2=模糊/一般, 3=正确/容易
     */
    @NotNull(message = "质量评分不能为空")
    @Min(value = 1, message = "质量评分不能小于1")
    @Max(value = 3, message = "质量评分不能大于3")
    private Integer qualityRating;

    /**
     * 用时(秒)
     */
    @NotNull(message = "用时不能为空")
    @Min(value = 1, message = "用时不能小于1秒")
    @Max(value = 3600, message = "用时不能超过3600秒")
    private Integer timeSpentSeconds;

    /**
     * 错误原因(如果答错)
     */
    @Size(max = 500, message = "错误原因长度不能超过500个字符")
    private String mistakeReason;

    /**
     * 学习笔记
     */
    @Size(max = 1000, message = "学习笔记长度不能超过1000个字符")
    private String studyNotes;

    /**
     * 难度感知 (1-5)
     * 用户主观感受的题目难度
     */
    @Min(value = 1, message = "难度感知不能小于1")
    @Max(value = 5, message = "难度感知不能大于5")
    private Integer perceivedDifficulty;

    /**
     * 是否跳过
     */
    private Boolean isSkipped = false;

    /**
     * 提交方式
     * NORMAL:正常提交, SKIP:跳过, AUTO:自动提交
     */
    @Pattern(regexp = "^(NORMAL|SKIP|AUTO)$", message = "提交方式必须是NORMAL、SKIP或AUTO")
    private String submissionType = "NORMAL";

    /**
     * 题目类型
     * text:文本题, choice:选择题, code:编程题
     */
    @NotBlank(message = "题目类型不能为空")
    @Pattern(regexp = "^(text|choice|code)$", message = "题目类型必须是text、choice或code")
    private String questionType;

    // 构造函数
    public SubmitAnswerRequest() {}

    public SubmitAnswerRequest(String sessionId, String knowledgePointId, Integer questionIndex, 
                              Integer qualityRating, Integer timeSpentSeconds, String questionType) {
        this.sessionId = sessionId;
        this.knowledgePointId = knowledgePointId;
        this.questionIndex = questionIndex;
        this.qualityRating = qualityRating;
        this.timeSpentSeconds = timeSpentSeconds;
        this.questionType = questionType;
    }

    // Getter和Setter方法
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(Integer qualityRating) {
        this.qualityRating = qualityRating;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public String getMistakeReason() {
        return mistakeReason;
    }

    public void setMistakeReason(String mistakeReason) {
        this.mistakeReason = mistakeReason;
    }

    public String getStudyNotes() {
        return studyNotes;
    }

    public void setStudyNotes(String studyNotes) {
        this.studyNotes = studyNotes;
    }

    public Integer getPerceivedDifficulty() {
        return perceivedDifficulty;
    }

    public void setPerceivedDifficulty(Integer perceivedDifficulty) {
        this.perceivedDifficulty = perceivedDifficulty;
    }

    public Boolean getIsSkipped() {
        return isSkipped;
    }

    public void setIsSkipped(Boolean isSkipped) {
        this.isSkipped = isSkipped;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @Override
    public String toString() {
        return "SubmitAnswerRequest{" +
                "sessionId='" + sessionId + '\'' +
                ", knowledgePointId='" + knowledgePointId + '\'' +
                ", questionIndex=" + questionIndex +
                ", isCorrect=" + isCorrect +
                ", qualityRating=" + qualityRating +
                ", timeSpentSeconds=" + timeSpentSeconds +
                ", questionType='" + questionType + '\'' +
                ", submissionType='" + submissionType + '\'' +
                ", isSkipped=" + isSkipped +
                '}';
    }
} 