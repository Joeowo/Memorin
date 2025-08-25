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
 * 高优先级错题数据源策略
 * 获取用户需要重点复习的高优先级错题（错误次数较多的未解决错题）
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class HighPriorityMistakesDataSourceStrategy implements DataSourceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(HighPriorityMistakesDataSourceStrategy.class);
    
    @Autowired
    private MistakeManagementService mistakeManagementService;

    @Override
    public String getStrategyName() {
        return "high-priority-mistakes";
    }

    @Override
    public String getDescription() {
        return "获取需要重点复习的高优先级错题，基于错误次数和准确率筛选";
    }

    @Override
    public List<QuestionItem> getBaseQuestions(Map<String, Object> params, String userId) {
        logger.info("获取用户高优先级错题: userId={}", userId);
        
        try {
            // 获取错误次数阈值参数，默认为3
            Integer threshold = getThresholdFromParams(params);
            
            // 获取高优先级错题
            List<MistakeRecord> mistakes = mistakeManagementService.getHighPriorityMistakes(userId, threshold);
            
            if (mistakes.isEmpty()) {
                logger.info("用户暂无高优先级错题: userId={}, threshold={}", userId, threshold);
                return Collections.emptyList();
            }
            
            // 转换为QuestionItem列表
            List<QuestionItem> questions = mistakes.stream()
                    .map(this::convertMistakeToQuestionItem)
                    .collect(Collectors.toList());
            
            logger.info("成功获取高优先级错题: userId={}, 阈值={}, 错题数量={}", userId, threshold, questions.size());
            return questions;
            
        } catch (Exception e) {
            logger.error("获取高优先级错题失败: userId={}, error={}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void validateParams(Map<String, Object> params) throws IllegalArgumentException {
        if (params != null && params.containsKey("threshold")) {
            Object thresholdObj = params.get("threshold");
            if (thresholdObj != null) {
                try {
                    int threshold = Integer.parseInt(thresholdObj.toString());
                    if (threshold < 1) {
                        throw new IllegalArgumentException("错误次数阈值必须大于等于1");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("错误次数阈值必须是有效的整数");
                }
            }
        }
    }

    @Override
    public Map<String, Class<?>> getSupportedParams() {
        Map<String, Class<?>> supportedParams = new HashMap<>();
        supportedParams.put("threshold", Integer.class);
        supportedParams.put("maxCount", Integer.class);
        supportedParams.put("sortBy", String.class);
        return supportedParams;
    }

    @Override
    public boolean requiresAuthentication() {
        return true; // 需要用户身份验证
    }

    @Override
    public int getEstimatedQuestionCount(Map<String, Object> params, String userId) {
        try {
            Integer threshold = getThresholdFromParams(params);
            // 这里可以实现更精确的估算，暂时使用简化逻辑
            List<MistakeRecord> mistakes = mistakeManagementService.getHighPriorityMistakes(userId, threshold);
            return mistakes.size();
            
        } catch (Exception e) {
            logger.warn("估算高优先级错题数量失败: userId={}, error={}", userId, e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean supportsIncremental() {
        return false; // 错题数据不支持增量获取
    }

    /**
     * 从参数中获取错误次数阈值
     */
    private Integer getThresholdFromParams(Map<String, Object> params) {
        if (params != null && params.containsKey("threshold")) {
            try {
                return Integer.parseInt(params.get("threshold").toString());
            } catch (NumberFormatException e) {
                logger.warn("无效的阈值参数，使用默认值: {}", params.get("threshold"));
            }
        }
        return 3; // 默认阈值为3次错误
    }

    /**
     * 将错题记录转换为题目项
     */
    private QuestionItem convertMistakeToQuestionItem(MistakeRecord mistake) {
        QuestionItem question = new QuestionItem();
        
        // 基本信息
        question.setKnowledgePointId(mistake.getKnowledgePointId());
        question.setQuestionType(mistake.getQuestionType() != null ? mistake.getQuestionType() : "text");
        
        // 处理难度级别转换
        Integer difficultyLevel = convertDifficultyToInteger(mistake.getDifficultyLevel());
        question.setDifficulty(difficultyLevel);
        
        // 高优先级错题特有信息
        question.setTitle("重点复习 - " + mistake.getKnowledgePointId());
        question.setContent(String.format("【高优先级】这道题已经错了%d次，需要重点练习！上次错误原因: %s", 
            mistake.getMistakeCount(),
            mistake.getMistakeReasons() != null ? mistake.getMistakeReasons() : "无"));
        
        // 处理标签转换
        List<String> tagsList = convertTagsToList(mistake.getMistakeTags());
        // 添加高优先级标签
        if (tagsList.isEmpty()) {
            tagsList = java.util.Arrays.asList("高优先级", "重点复习");
        } else {
            tagsList.add("高优先级");
            tagsList.add("重点复习");
        }
        question.setTags(tagsList);
        
        // 复习信息
        QuestionItem.ReviewInfo reviewInfo = new QuestionItem.ReviewInfo();
        reviewInfo.setLastReviewTime(mistake.getLastMistakeTime());
        reviewInfo.setReviewCount(mistake.getTotalReviewCount());
        reviewInfo.setCorrectCount(mistake.getCorrectCount());
        
        question.setReviewInfo(reviewInfo);
        
        // 元数据 - 添加高优先级相关信息
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "high-priority-mistake");
        metadata.put("sourceId", mistake.getId());
        metadata.put("mistakeId", mistake.getId());
        metadata.put("priority", "HIGH");
        metadata.put("urgencyLevel", calculateUrgencyLevel(mistake));
        metadata.put("mistakeCount", mistake.getMistakeCount());
        metadata.put("errorRate", mistake.getErrorRate());
        metadata.put("accuracyRate", mistake.getAccuracyRate());
        metadata.put("masteryStatus", mistake.getMasteryStatus());
        metadata.put("isWeakPoint", true); // 高优先级错题都是薄弱点
        metadata.put("reviewRecommendation", generateReviewRecommendation(mistake));
        metadata.put("firstMistakeTime", mistake.getFirstMistakeTime());
        metadata.put("lastMistakeTime", mistake.getLastMistakeTime());
        metadata.put("userNotes", mistake.getUserNotes());
        
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
     * 计算紧急程度等级
     */
    private String calculateUrgencyLevel(MistakeRecord mistake) {
        int mistakeCount = mistake.getMistakeCount();
        double accuracyRate = mistake.getAccuracyRate();
        
        if (mistakeCount >= 5 || accuracyRate < 0.3) {
            return "CRITICAL"; // 极其紧急
        } else if (mistakeCount >= 4 || accuracyRate < 0.5) {
            return "HIGH"; // 高度紧急
        } else if (mistakeCount >= 3 || accuracyRate < 0.7) {
            return "MEDIUM"; // 中度紧急
        } else {
            return "LOW"; // 低度紧急
        }
    }

    /**
     * 生成复习建议
     */
    private String generateReviewRecommendation(MistakeRecord mistake) {
        int mistakeCount = mistake.getMistakeCount();
        double accuracyRate = mistake.getAccuracyRate();
        
        StringBuilder recommendation = new StringBuilder();
        
        if (mistakeCount >= 5) {
            recommendation.append("建议进行深度学习：仔细研读相关理论，做大量相似练习。");
        } else if (mistakeCount >= 3) {
            recommendation.append("建议加强练习：重点关注错误原因，反复练习相关知识点。");
        }
        
        if (accuracyRate < 0.3) {
            recommendation.append("正确率极低，建议回到基础概念学习。");
        } else if (accuracyRate < 0.5) {
            recommendation.append("正确率偏低，建议系统复习相关知识。");
        }
        
        if (recommendation.length() == 0) {
            recommendation.append("需要重点复习，建议认真对待每次练习机会。");
        }
        
        return recommendation.toString();
    }

    /**
     * 将难度字符串转换为数字
     */
    private Integer convertDifficultyToInteger(String difficultyStr) {
        if (difficultyStr == null || difficultyStr.trim().isEmpty()) {
            return 4; // 高优先级错题默认难度较高
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
                try {
                    return Integer.parseInt(difficultyStr);
                } catch (NumberFormatException e) {
                    return 4; // 高优先级错题默认难度较高
                }
        }
    }

    /**
     * 将标签字符串转换为列表
     */
    private List<String> convertTagsToList(String tagsStr) {
        if (tagsStr == null || tagsStr.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        String[] tagArray = tagsStr.split("[,;\\s]+");
        return java.util.Arrays.stream(tagArray)
                .filter(tag -> !tag.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toCollection(java.util.ArrayList::new));
    }
} 