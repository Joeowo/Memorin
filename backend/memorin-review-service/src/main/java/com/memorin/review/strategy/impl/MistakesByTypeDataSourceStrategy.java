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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 按类型错题数据源策略
 * 根据题目类型获取相应的错题记录，支持特定类型的错题专项复习
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class MistakesByTypeDataSourceStrategy implements DataSourceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(MistakesByTypeDataSourceStrategy.class);
    
    @Autowired
    private MistakeManagementService mistakeManagementService;

    // 支持的题目类型
    private static final Set<String> SUPPORTED_QUESTION_TYPES = new HashSet<>(Arrays.asList(
        "text", "choice", "code", "essay", "calculation", "diagram", "audio", "video"
    ));

    @Override
    public String getStrategyName() {
        return "mistakes-by-type";
    }

    @Override
    public String getDescription() {
        return "根据题目类型获取错题记录，支持特定类型的错题专项复习";
    }

    @Override
    public List<QuestionItem> getBaseQuestions(Map<String, Object> params, String userId) {
        logger.info("按类型获取用户错题: userId={}", userId);
        
        try {
            // 获取题目类型参数
            String questionType = getQuestionTypeFromParams(params);
            
            // 获取该类型的所有错题
            List<MistakeRecord> allMistakes = mistakeManagementService.getUnresolvedMistakes(userId);
            
            // 按题目类型过滤
            List<MistakeRecord> typedMistakes = allMistakes.stream()
                    .filter(mistake -> isMatchingType(mistake, questionType))
                    .collect(Collectors.toList());
            
            if (typedMistakes.isEmpty()) {
                logger.info("用户在该类型下暂无错题: userId={}, questionType={}", userId, questionType);
                return Collections.emptyList();
            }
            
            // 转换为QuestionItem列表
            List<QuestionItem> questions = typedMistakes.stream()
                    .map(mistake -> convertMistakeToQuestionItem(mistake, questionType))
                    .collect(Collectors.toList());
            
            logger.info("成功获取指定类型错题: userId={}, questionType={}, 错题数量={}", 
                       userId, questionType, questions.size());
            return questions;
            
        } catch (Exception e) {
            logger.error("按类型获取错题失败: userId={}, error={}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void validateParams(Map<String, Object> params) throws IllegalArgumentException {
        if (params == null || !params.containsKey("questionType")) {
            throw new IllegalArgumentException("必须指定题目类型参数 'questionType'");
        }
        
        String questionType = params.get("questionType").toString().toLowerCase();
        if (!SUPPORTED_QUESTION_TYPES.contains(questionType) && !"all".equals(questionType)) {
            throw new IllegalArgumentException("不支持的题目类型: " + questionType + 
                ", 支持的类型: " + String.join(", ", SUPPORTED_QUESTION_TYPES) + ", all");
        }
    }

    @Override
    public Map<String, Class<?>> getSupportedParams() {
        Map<String, Class<?>> supportedParams = new HashMap<>();
        supportedParams.put("questionType", String.class);
        supportedParams.put("includeDifficulty", String.class);
        supportedParams.put("sortBy", String.class);
        supportedParams.put("maxCount", Integer.class);
        return supportedParams;
    }

    @Override
    public boolean requiresAuthentication() {
        return true; // 需要用户身份验证
    }

    @Override
    public int getEstimatedQuestionCount(Map<String, Object> params, String userId) {
        try {
            String questionType = getQuestionTypeFromParams(params);
            
            // 快速估算：获取用户该类型错题数量
            List<MistakeRecord> allMistakes = mistakeManagementService.getUnresolvedMistakes(userId);
            long count = allMistakes.stream()
                    .filter(mistake -> isMatchingType(mistake, questionType))
                    .count();
            
            return (int) count;
            
        } catch (Exception e) {
            logger.warn("估算类型错题数量失败: userId={}, error={}", userId, e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean supportsIncremental() {
        return false; // 错题数据不支持增量获取
    }

    /**
     * 从参数中获取题目类型
     */
    private String getQuestionTypeFromParams(Map<String, Object> params) {
        if (params != null && params.containsKey("questionType")) {
            return params.get("questionType").toString().toLowerCase();
        }
        return "all"; // 默认获取所有类型
    }

    /**
     * 检查错题是否匹配指定类型
     */
    private boolean isMatchingType(MistakeRecord mistake, String targetType) {
        if ("all".equals(targetType)) {
            return true; // 获取所有类型
        }
        
        String mistakeType = mistake.getQuestionType();
        if (mistakeType == null || mistakeType.trim().isEmpty()) {
            return false;
        }
        
        return targetType.equalsIgnoreCase(mistakeType.toLowerCase());
    }

    /**
     * 将错题记录转换为题目项
     */
    private QuestionItem convertMistakeToQuestionItem(MistakeRecord mistake, String requestedType) {
        QuestionItem question = new QuestionItem();
        
        // 基本信息
        question.setKnowledgePointId(mistake.getKnowledgePointId());
        question.setQuestionType(mistake.getQuestionType() != null ? mistake.getQuestionType() : "text");
        
        // 处理难度级别转换
        Integer difficultyLevel = convertDifficultyToInteger(mistake.getDifficultyLevel());
        question.setDifficulty(difficultyLevel);
        
        // 类型特定信息
        String typeDisplayName = getTypeDisplayName(mistake.getQuestionType());
        question.setTitle(String.format("%s错题复习 - %s", typeDisplayName, mistake.getKnowledgePointId()));
        question.setContent(String.format("【%s专项】错误%d次的%s题目，重点练习！错误原因: %s", 
            typeDisplayName,
            mistake.getMistakeCount(),
            typeDisplayName,
            mistake.getMistakeReasons() != null ? mistake.getMistakeReasons() : "无"));
        
        // 处理标签转换，添加类型相关标签
        List<String> tagsList = convertTagsToList(mistake.getMistakeTags());
        tagsList.add(typeDisplayName + "专项");
        tagsList.add("错题复习");
        if (mistake.getMistakeCount() >= 3) {
            tagsList.add("重点关注");
        }
        question.setTags(tagsList);
        
        // 设置分类
        question.setCategory(typeDisplayName + "错题");
        
        // 复习信息
        QuestionItem.ReviewInfo reviewInfo = new QuestionItem.ReviewInfo();
        reviewInfo.setLastReviewTime(mistake.getLastMistakeTime());
        reviewInfo.setReviewCount(mistake.getTotalReviewCount());
        reviewInfo.setCorrectCount(mistake.getCorrectCount());
        
        question.setReviewInfo(reviewInfo);
        
        // 元数据 - 添加类型相关信息
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "mistakes-by-type");
        metadata.put("sourceId", mistake.getId());
        metadata.put("mistakeId", mistake.getId());
        metadata.put("targetQuestionType", requestedType);
        metadata.put("actualQuestionType", mistake.getQuestionType());
        metadata.put("typeDisplayName", typeDisplayName);
        metadata.put("specialization", typeDisplayName + "专项复习");
        metadata.put("mistakeCount", mistake.getMistakeCount());
        metadata.put("errorRate", mistake.getErrorRate());
        metadata.put("accuracyRate", mistake.getAccuracyRate());
        metadata.put("masteryStatus", mistake.getMasteryStatus());
        metadata.put("priority", calculateTypePriority(mistake));
        metadata.put("typeRecommendation", generateTypeSpecificRecommendation(mistake));
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
     * 获取题目类型的显示名称
     */
    private String getTypeDisplayName(String questionType) {
        if (questionType == null || questionType.trim().isEmpty()) {
            return "文本";
        }
        
        switch (questionType.toLowerCase()) {
            case "text":
                return "文本";
            case "choice":
                return "选择";
            case "code":
                return "编程";
            case "essay":
                return "问答";
            case "calculation":
                return "计算";
            case "diagram":
                return "图表";
            case "audio":
                return "听力";
            case "video":
                return "视频";
            default:
                return questionType;
        }
    }

    /**
     * 计算类型相关的优先级
     */
    private String calculateTypePriority(MistakeRecord mistake) {
        String questionType = mistake.getQuestionType();
        int mistakeCount = mistake.getMistakeCount();
        
        // 编程题和计算题的错误影响更大
        if ("code".equals(questionType) || "calculation".equals(questionType)) {
            return mistakeCount >= 2 ? "HIGH" : "MEDIUM";
        }
        
        // 选择题相对简单，错误较多时才高优先级
        if ("choice".equals(questionType)) {
            return mistakeCount >= 4 ? "HIGH" : "LOW";
        }
        
        // 其他类型的标准优先级判断
        return mistakeCount >= 3 ? "HIGH" : "MEDIUM";
    }

    /**
     * 生成类型特定的复习建议
     */
    private String generateTypeSpecificRecommendation(MistakeRecord mistake) {
        String questionType = mistake.getQuestionType();
        int mistakeCount = mistake.getMistakeCount();
        
        StringBuilder recommendation = new StringBuilder();
        
        if (questionType != null) {
            switch (questionType.toLowerCase()) {
                case "choice":
                    recommendation.append("选择题建议：仔细审题，排除干扰选项，注意关键词。");
                    break;
                case "code":
                    recommendation.append("编程题建议：多练习语法，理解算法逻辑，注意边界条件。");
                    break;
                case "calculation":
                    recommendation.append("计算题建议：熟练掌握公式，多做练习，检查计算过程。");
                    break;
                case "essay":
                    recommendation.append("问答题建议：理解题意，结构清晰，要点完整。");
                    break;
                default:
                    recommendation.append("建议：针对该类型题目加强专项练习。");
            }
        }
        
        if (mistakeCount >= 3) {
            recommendation.append("已错误多次，建议深入学习相关知识点。");
        }
        
        return recommendation.toString();
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
        List<String> tagsList = new ArrayList<>();
        
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            String[] tagArray = tagsStr.split("[,;\\s]+");
            tagsList = Arrays.stream(tagArray)
                    .filter(tag -> !tag.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        
        return tagsList;
    }
} 