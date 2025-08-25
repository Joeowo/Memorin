package com.memorin.review.strategy.impl;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import com.memorin.review.entity.MistakeRecord;
import com.memorin.review.service.MistakeManagementService;
import com.memorin.review.strategy.DataSourceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全部错题数据源策略
 * 获取用户所有未解决的错题记录，转换为题目列表
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class AllMistakesDataSourceStrategy implements DataSourceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(AllMistakesDataSourceStrategy.class);
    
    @Autowired
    private MistakeManagementService mistakeManagementService;

    @Override
    public String getStrategyName() {
        return "all-mistakes";
    }

    @Override
    public String getDescription() {
        return "获取用户所有未解决的错题记录，用于错题专项复习";
    }

    @Override
    public List<QuestionItem> getBaseQuestions(Map<String, Object> params, String userId) {
        logger.info("获取用户全部错题: userId={}", userId);
        
        try {
            // 获取用户所有未解决错题
            List<MistakeRecord> mistakes = mistakeManagementService.getUnresolvedMistakes(userId);
            
            if (mistakes.isEmpty()) {
                logger.info("用户暂无未解决错题: userId={}", userId);
                return Collections.emptyList();
            }
            
            // 转换为QuestionItem列表
            List<QuestionItem> questions = mistakes.stream()
                    .map(this::convertMistakeToQuestionItem)
                    .collect(Collectors.toList());
            
            logger.info("成功获取错题数据: userId={}, 错题数量={}", userId, questions.size());
            return questions;
            
        } catch (Exception e) {
            logger.error("获取错题数据失败: userId={}, error={}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void validateParams(Map<String, Object> params) throws IllegalArgumentException {
        // 全部错题数据源不需要特殊参数验证
        // 如果有必需参数，在这里验证并抛出异常
    }

    @Override
    public Map<String, Class<?>> getSupportedParams() {
        Map<String, Class<?>> supportedParams = new HashMap<>();
        supportedParams.put("includeResolved", Boolean.class);
        supportedParams.put("sortBy", String.class);
        supportedParams.put("sortOrder", String.class);
        return supportedParams;
    }

    @Override
    public boolean requiresAuthentication() {
        return true; // 需要用户身份验证
    }

    @Override
    public int getEstimatedQuestionCount(Map<String, Object> params, String userId) {
        try {
            // 获取用户未解决错题数量的快速估算
            Map<String, Object> stats = mistakeManagementService.getMistakeStatistics(userId);
            Long unresolvedCount = (Long) stats.get("unresolvedMistakes");
            return unresolvedCount != null ? unresolvedCount.intValue() : 0;
            
        } catch (Exception e) {
            logger.warn("估算错题数量失败: userId={}, error={}", userId, e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean supportsIncremental() {
        return false; // 错题数据不支持增量获取
    }

    /**
     * 将错题记录转换为题目项
     */
    private QuestionItem convertMistakeToQuestionItem(MistakeRecord mistake) {
        QuestionItem question = new QuestionItem();
        
        // 基本信息
        question.setKnowledgePointId(mistake.getKnowledgePointId());
        question.setQuestionType(mistake.getQuestionType() != null ? mistake.getQuestionType() : "text");
        
        // 处理难度级别转换 (字符串 -> 数字)
        Integer difficultyLevel = convertDifficultyToInteger(mistake.getDifficultyLevel());
        question.setDifficulty(difficultyLevel);
        
        // 错题特有信息
        question.setTitle("错题复习 - " + mistake.getKnowledgePointId());
        question.setContent("重新练习这道错题，上次错误原因: " + 
                          (mistake.getMistakeReasons() != null ? mistake.getMistakeReasons() : "无"));
        
        // 处理标签转换 (字符串 -> 列表)
        List<String> tagsList = convertTagsToList(mistake.getMistakeTags());
        question.setTags(tagsList);
        
        // 复习信息
        QuestionItem.ReviewInfo reviewInfo = new QuestionItem.ReviewInfo();
        // 使用实际存在的字段
        reviewInfo.setLastReviewTime(mistake.getLastMistakeTime()); // LocalDateTime类型
        reviewInfo.setReviewCount(mistake.getTotalReviewCount());
        reviewInfo.setCorrectCount(mistake.getCorrectCount());
        
        question.setReviewInfo(reviewInfo);
        
        // 元数据 - 使用Map<String, Object>
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "mistake-record");
        metadata.put("sourceId", mistake.getId());
        metadata.put("mistakeId", mistake.getId());
        metadata.put("firstMistakeTime", mistake.getFirstMistakeTime());
        metadata.put("isResolved", mistake.getIsResolved());
        metadata.put("errorRate", mistake.getErrorRate());
        metadata.put("accuracyRate", mistake.getAccuracyRate());
        metadata.put("masteryStatus", mistake.getMasteryStatus());
        metadata.put("mistakeCount", mistake.getMistakeCount());
        metadata.put("userNotes", mistake.getUserNotes());
        metadata.put("priority", mistake.getMistakeCount() >= 3 ? "HIGH" : "MEDIUM");
        metadata.put("isWeakPoint", mistake.getAccuracyRate() < 0.6);
        
        if (mistake.getCreatedAt() != null) {
            metadata.put("createdAt", mistake.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (mistake.getUpdatedAt() != null) {
            metadata.put("updatedAt", mistake.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        
        question.setMetadata(metadata);
        
        return question;
    }

    /**
     * 将难度字符串转换为数字
     */
    private Integer convertDifficultyToInteger(String difficultyStr) {
        if (difficultyStr == null || difficultyStr.trim().isEmpty()) {
            return 3; // 默认中等难度
        }
        
        switch (difficultyStr.toLowerCase()) {
            case "very_easy":
            case "极易":
                return 1;
            case "easy":
            case "简单":
                return 2;
            case "medium":
            case "中等":
                return 3;
            case "hard":
            case "困难":
                return 4;
            case "very_hard":
            case "极难":
                return 5;
            default:
                // 尝试直接解析数字
                try {
                    return Integer.parseInt(difficultyStr);
                } catch (NumberFormatException e) {
                    return 3; // 默认中等难度
                }
        }
    }

    /**
     * 将标签字符串转换为列表
     */
    private List<String> convertTagsToList(String tagsStr) {
        if (tagsStr == null || tagsStr.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        // 支持多种分隔符：逗号、分号、空格
        String[] tagArray = tagsStr.split("[,;\\s]+");
        return java.util.Arrays.stream(tagArray)
                .filter(tag -> !tag.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());
    }
} 