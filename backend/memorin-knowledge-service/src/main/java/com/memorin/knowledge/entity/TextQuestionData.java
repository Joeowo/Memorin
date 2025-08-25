package com.memorin.knowledge.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 文本题数据实体 - 统一处理填空题和问答题
 * 与BaseKnowledge通过knowledgeId关联，存储题型特定的数据
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "text_question_data", indexes = {
    @Index(name = "idx_text_knowledge_id", columnList = "knowledge_id"),
    @Index(name = "idx_text_type", columnList = "text_type"),
    @Index(name = "idx_text_validation_mode", columnList = "validation_mode")
})
public class TextQuestionData {

    /**
     * 数据记录ID
     * 格式: TQD_{date}_{seq}
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 关联的知识点ID - 外键关联BaseKnowledge
     */
    @NotBlank(message = "关联知识点ID不能为空")
    @Column(name = "knowledge_id", length = 50, nullable = false)
    private String knowledgeId;

    /**
     * 文本题类型 - fill:填空题, essay:问答题
     */
    @NotBlank(message = "文本题类型不能为空")
    @Pattern(regexp = "^(fill|essay)$", message = "文本题类型必须是fill或essay")
    @Column(name = "text_type", length = 20, nullable = false)
    private String textType;

    /**
     * 标准答案/参考答案
     */
    @NotBlank(message = "标准答案不能为空")
    @Size(max = 2000, message = "标准答案长度不能超过2000个字符")
    @Column(name = "answer", length = 2000, nullable = false)
    private String answer;

    /**
     * 答案验证模式
     * exact: 完全匹配, contains: 包含匹配, regex: 正则匹配, manual: 手动评分
     */
    @NotBlank(message = "答案验证模式不能为空")
    @Pattern(regexp = "^(exact|contains|regex|manual)$", message = "答案验证模式必须是exact、contains、regex或manual")
    @Column(name = "validation_mode", length = 20, nullable = false)
    private String validationMode;

    /**
     * 可接受的同义答案(填空题) - 存储为逗号分隔的字符串
     */
    @Size(max = 1000, message = "同义答案总长度不能超过1000个字符")
    @Column(name = "alternative_answers", length = 1000)
    private String alternativeAnswers;

    /**
     * 答案是否区分大小写(填空题) - 默认不区分
     */
    @Column(name = "case_sensitive", nullable = false)
    private Boolean caseSensitive = false;

    /**
     * 正则表达式模式 - 当validationMode为regex时使用
     */
    @Size(max = 500, message = "正则表达式模式长度不能超过500个字符")
    @Column(name = "regex_pattern", length = 500)
    private String regexPattern;

    /**
     * 评分要点(问答题) - 使用ElementCollection存储
     */
    @ElementCollection
    @CollectionTable(
        name = "text_question_scoring_points",
        joinColumns = @JoinColumn(name = "text_question_id")
    )
    private List<ScoringPoint> scoringPoints = new ArrayList<>();

    /**
     * 最大字数限制
     */
    @Min(value = 1, message = "最大字数限制不能小于1")
    @Max(value = 10000, message = "最大字数限制不能超过10000")
    @Column(name = "max_word_count")
    private Integer maxWordCount;

    /**
     * 最小字数要求
     */
    @Min(value = 1, message = "最小字数要求不能小于1")
    @Max(value = 10000, message = "最小字数要求不能超过10000")
    @Column(name = "min_word_count")
    private Integer minWordCount;

    /**
     * 创建时间戳
     */
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    /**
     * 更新时间戳
     */
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    // 构造函数
    public TextQuestionData() {
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    // JPA生命周期回调
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 获取同义答案列表 - 将逗号分隔的字符串转换为List
     */
    public List<String> getAlternativeAnswerList() {
        List<String> answerList = new ArrayList<>();
        if (alternativeAnswers != null && !alternativeAnswers.trim().isEmpty()) {
            String[] answerArray = alternativeAnswers.split(",");
            for (String answer : answerArray) {
                String trimmedAnswer = answer.trim();
                if (!trimmedAnswer.isEmpty()) {
                    answerList.add(trimmedAnswer);
                }
            }
        }
        return answerList;
    }

    /**
     * 设置同义答案列表 - 将List转换为逗号分隔的字符串
     */
    public void setAlternativeAnswerList(List<String> answerList) {
        if (answerList == null || answerList.isEmpty()) {
            this.alternativeAnswers = "";
        } else {
            this.alternativeAnswers = String.join(",", answerList);
        }
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
     * 获取评分要点总分值
     */
    public int getTotalScoringPoints() {
        if (scoringPoints == null || scoringPoints.isEmpty()) {
            return 0;
        }
        return scoringPoints.stream()
                .mapToInt(ScoringPoint::getPoints)
                .sum();
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

    public String getAlternativeAnswers() {
        return alternativeAnswers;
    }

    public void setAlternativeAnswers(String alternativeAnswers) {
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

    public List<ScoringPoint> getScoringPoints() {
        return scoringPoints;
    }

    public void setScoringPoints(List<ScoringPoint> scoringPoints) {
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

    @Override
    public String toString() {
        return "TextQuestionData{" +
                "id='" + id + '\'' +
                ", knowledgeId='" + knowledgeId + '\'' +
                ", textType='" + textType + '\'' +
                ", validationMode='" + validationMode + '\'' +
                ", caseSensitive=" + caseSensitive +
                ", maxWordCount=" + maxWordCount +
                ", minWordCount=" + minWordCount +
                '}';
    }
} 