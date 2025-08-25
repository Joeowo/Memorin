package com.memorin.knowledge.dto.response;

import com.memorin.knowledge.entity.TextQuestionData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文本题数据响应DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class TextQuestionDataResponse {

    /**
     * 数据记录ID
     */
    private String id;

    /**
     * 关联的知识点ID
     */
    private String knowledgeId;

    /**
     * 文本题类型 - fill:填空题, essay:问答题
     */
    private String textType;

    /**
     * 标准答案/参考答案
     */
    private String answer;

    /**
     * 答案验证模式
     */
    private String validationMode;

    /**
     * 可接受的同义答案(填空题)
     */
    private List<String> alternativeAnswers;

    /**
     * 答案是否区分大小写(填空题)
     */
    private Boolean caseSensitive;

    /**
     * 正则表达式模式
     */
    private String regexPattern;

    /**
     * 评分要点(问答题)
     */
    private List<ScoringPointResponse> scoringPoints;

    /**
     * 最大字数限制
     */
    private Integer maxWordCount;

    /**
     * 最小字数要求
     */
    private Integer minWordCount;

    /**
     * 创建时间戳
     */
    private Long createdAt;

    /**
     * 更新时间戳
     */
    private Long updatedAt;

    /**
     * 评分要点总分值
     */
    private Integer totalScoringPoints;

    // 构造函数
    public TextQuestionDataResponse() {}

    /**
     * 从TextQuestionData实体转换为TextQuestionDataResponse
     */
    public static TextQuestionDataResponse fromEntity(TextQuestionData textData) {
        TextQuestionDataResponse response = new TextQuestionDataResponse();
        response.setId(textData.getId());
        response.setKnowledgeId(textData.getKnowledgeId());
        response.setTextType(textData.getTextType());
        response.setAnswer(textData.getAnswer());
        response.setValidationMode(textData.getValidationMode());
        response.setAlternativeAnswers(textData.getAlternativeAnswerList()); // 使用实体的getAlternativeAnswerList方法
        response.setCaseSensitive(textData.getCaseSensitive());
        response.setRegexPattern(textData.getRegexPattern());
        
        // 转换评分要点
        if (textData.getScoringPoints() != null) {
            response.setScoringPoints(
                textData.getScoringPoints().stream()
                    .map(ScoringPointResponse::fromEntity)
                    .collect(Collectors.toList())
            );
        }
        
        response.setMaxWordCount(textData.getMaxWordCount());
        response.setMinWordCount(textData.getMinWordCount());
        response.setCreatedAt(textData.getCreatedAt());
        response.setUpdatedAt(textData.getUpdatedAt());
        response.setTotalScoringPoints(textData.getTotalScoringPoints());
        
        return response;
    }

    /**
     * 检查是否为填空题
     */
    public boolean isFillQuestion() {
        return "fill".equals(this.textType);
    }

    /**
     * 检查是否为问答题
     */
    public boolean isEssayQuestion() {
        return "essay".equals(this.textType);
    }

    /**
     * 检查是否有评分要点
     */
    public boolean hasScoringPoints() {
        return scoringPoints != null && !scoringPoints.isEmpty();
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

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(String validationMode) {
        this.validationMode = validationMode;
    }

    public List<String> getAlternativeAnswers() {
        return alternativeAnswers;
    }

    public void setAlternativeAnswers(List<String> alternativeAnswers) {
        this.alternativeAnswers = alternativeAnswers;
    }

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public List<ScoringPointResponse> getScoringPoints() {
        return scoringPoints;
    }

    public void setScoringPoints(List<ScoringPointResponse> scoringPoints) {
        this.scoringPoints = scoringPoints;
    }

    public Integer getMaxWordCount() {
        return maxWordCount;
    }

    public void setMaxWordCount(Integer maxWordCount) {
        this.maxWordCount = maxWordCount;
    }

    public Integer getMinWordCount() {
        return minWordCount;
    }

    public void setMinWordCount(Integer minWordCount) {
        this.minWordCount = minWordCount;
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

    public Integer getTotalScoringPoints() {
        return totalScoringPoints;
    }

    public void setTotalScoringPoints(Integer totalScoringPoints) {
        this.totalScoringPoints = totalScoringPoints;
    }

    @Override
    public String toString() {
        return "TextQuestionDataResponse{" +
                "id='" + id + '\'' +
                ", knowledgeId='" + knowledgeId + '\'' +
                ", textType='" + textType + '\'' +
                ", validationMode='" + validationMode + '\'' +
                ", caseSensitive=" + caseSensitive +
                ", maxWordCount=" + maxWordCount +
                ", minWordCount=" + minWordCount +
                ", totalScoringPoints=" + totalScoringPoints +
                '}';
    }
} 