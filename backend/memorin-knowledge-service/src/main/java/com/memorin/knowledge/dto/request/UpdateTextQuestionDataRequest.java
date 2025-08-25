package com.memorin.knowledge.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 更新文本题数据请求DTO
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class UpdateTextQuestionDataRequest {

    /**
     * 文本题类型 - fill:填空题, essay:问答题
     */
    @NotBlank(message = "文本题类型不能为空")
    @Pattern(regexp = "^(fill|essay)$", message = "文本题类型必须是fill或essay")
    private String textType;

    /**
     * 标准答案/参考答案
     */
    @NotBlank(message = "标准答案不能为空")
    @Size(max = 2000, message = "标准答案长度不能超过2000个字符")
    private String answer;

    /**
     * 答案验证模式
     * exact: 完全匹配, contains: 包含匹配, regex: 正则匹配, manual: 手动评分
     */
    @NotBlank(message = "答案验证模式不能为空")
    @Pattern(regexp = "^(exact|contains|regex|manual)$", message = "答案验证模式必须是exact、contains、regex或manual")
    private String validationMode;

    /**
     * 可接受的同义答案(填空题) - 可选
     */
    @Size(max = 20, message = "同义答案数量不能超过20个")
    private List<String> alternativeAnswers;

    /**
     * 答案是否区分大小写(填空题) - 可选
     */
    private Boolean caseSensitive;

    /**
     * 正则表达式模式 - 当validationMode为regex时必填
     */
    @Size(max = 500, message = "正则表达式模式长度不能超过500个字符")
    private String regexPattern;

    /**
     * 评分要点(问答题) - 可选
     */
    @Valid
    @Size(max = 10, message = "评分要点数量不能超过10个")
    private List<ScoringPointRequest> scoringPoints;

    /**
     * 最大字数限制 - 可选
     */
    @Min(value = 1, message = "最大字数限制不能小于1")
    @Max(value = 10000, message = "最大字数限制不能超过10000")
    private Integer maxWordCount;

    /**
     * 最小字数要求 - 可选
     */
    @Min(value = 1, message = "最小字数要求不能小于1")
    @Max(value = 10000, message = "最小字数要求不能超过10000")
    private Integer minWordCount;

    // 构造函数
    public UpdateTextQuestionDataRequest() {}

    // Getter和Setter方法
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

    public List<ScoringPointRequest> getScoringPoints() {
        return scoringPoints;
    }

    public void setScoringPoints(List<ScoringPointRequest> scoringPoints) {
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
     * 检查是否需要正则表达式
     */
    public boolean needsRegexPattern() {
        return "regex".equals(this.validationMode);
    }

    @Override
    public String toString() {
        return "UpdateTextQuestionDataRequest{" +
                "textType='" + textType + '\'' +
                ", validationMode='" + validationMode + '\'' +
                ", caseSensitive=" + caseSensitive +
                ", maxWordCount=" + maxWordCount +
                ", minWordCount=" + minWordCount +
                ", scoringPointsCount=" + (scoringPoints != null ? scoringPoints.size() : 0) +
                '}';
    }
} 