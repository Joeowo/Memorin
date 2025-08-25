package com.memorin.review.strategy.impl;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import com.memorin.review.strategy.DataSourceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 模拟数据源策略实现
 * 用于测试和演示，生成模拟的题目数据
 * 不依赖真实的数据库或外部服务
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class MockDataSourceStrategy implements DataSourceStrategy {

    private static final Logger log = LoggerFactory.getLogger(MockDataSourceStrategy.class);

    @Override
    public String getStrategyName() {
        return "mock-data";
    }

    @Override
    public String getDescription() {
        return "模拟数据源（用于测试）";
    }

    @Override
    public List<QuestionItem> getBaseQuestions(Map<String, Object> params, String userId) throws Exception {
        log.debug("[模拟数据源] 开始生成模拟题目数据，用户: {}, 参数: {}", userId, params);

        int count = getQuestionCount(params);
        String category = getCategory(params);
        boolean includeReviewInfo = getIncludeReviewInfo(params);

        List<QuestionItem> questions = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            QuestionItem question = createMockQuestion(i, category, includeReviewInfo);
            questions.add(question);
        }

        log.debug("[模拟数据源] 生成完成，共 {} 道题目", questions.size());
        return questions;
    }

    @Override
    public void validateParams(Map<String, Object> params) throws IllegalArgumentException {
        if (params == null) {
            return; // 允许空参数，使用默认值
        }

        // 验证题目数量
        Object countObj = params.get("count");
        if (countObj != null) {
            try {
                int count = convertToInteger(countObj);
                if (count <= 0) {
                    throw new IllegalArgumentException("count 参数必须大于0，当前值: " + count);
                }
                if (count > 100) {
                    throw new IllegalArgumentException("count 参数不能超过100，当前值: " + count);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("count 参数必须是有效的整数，当前值: " + countObj);
            }
        }

        // 验证分类
        Object categoryObj = params.get("category");
        if (categoryObj != null && !(categoryObj instanceof String)) {
            throw new IllegalArgumentException("category 参数必须是字符串，当前类型: " + categoryObj.getClass().getSimpleName());
        }
    }

    @Override
    public Map<String, Class<?>> getSupportedParams() {
        Map<String, Class<?>> params = new HashMap<>();
        params.put("count", Integer.class);           // 生成题目数量
        params.put("category", String.class);         // 题目分类
        params.put("includeReviewInfo", Boolean.class); // 是否包含复习信息
        return params;
    }

    @Override
    public boolean requiresAuthentication() {
        return false; // 模拟数据源不需要认证
    }

    @Override
    public int getEstimatedQuestionCount(Map<String, Object> params, String userId) {
        return getQuestionCount(params);
    }

    @Override
    public boolean supportsIncremental() {
        return false; // 简单实现，不支持增量获取
    }

    /**
     * 获取题目数量参数
     */
    private int getQuestionCount(Map<String, Object> params) {
        if (params == null) {
            return 10; // 默认生成10道题目
        }

        Object countObj = params.get("count");
        if (countObj != null) {
            try {
                return convertToInteger(countObj);
            } catch (NumberFormatException e) {
                log.warn("[模拟数据源] count 参数无效: {}, 使用默认值10", countObj);
            }
        }

        return 10;
    }

    /**
     * 获取题目分类参数
     */
    private String getCategory(Map<String, Object> params) {
        if (params == null) {
            return "测试";
        }

        Object categoryObj = params.get("category");
        if (categoryObj instanceof String) {
            return (String) categoryObj;
        }

        return "测试";
    }

    /**
     * 获取是否包含复习信息参数
     */
    private boolean getIncludeReviewInfo(Map<String, Object> params) {
        if (params == null) {
            return true;
        }

        Object includeObj = params.get("includeReviewInfo");
        if (includeObj instanceof Boolean) {
            return (Boolean) includeObj;
        }

        return true;
    }

    /**
     * 创建模拟题目
     */
    private QuestionItem createMockQuestion(int index, String category, boolean includeReviewInfo) {
        QuestionItem question = new QuestionItem();
        
        // 基本信息
        question.setKnowledgePointId("MOCK_KP_" + String.format("%03d", index));
        question.setQuestionType(getRandomQuestionType(index));
        question.setTitle("模拟题目 " + index);
        question.setContent("这是第 " + index + " 道模拟题目的内容。请选择正确的答案。");
        question.setDifficulty(getRandomDifficulty(index));
        question.setKnowledgeBaseId("MOCK_BASE_001");
        question.setKnowledgeAreaId("MOCK_AREA_001");
        question.setCategory(category);

        // 设置选择题选项
        if ("choice".equals(question.getQuestionType())) {
            List<String> choices = Arrays.asList(
                "选项A - 正确答案",
                "选项B - 错误答案",
                "选项C - 错误答案", 
                "选项D - 错误答案"
            );
            question.setChoices(choices);
            question.setCorrectAnswer("A");
        } else {
            question.setCorrectAnswer("这是第 " + index + " 道题目的标准答案。");
        }

        // 设置标签
        List<String> tags = new ArrayList<>();
        tags.add("模拟");
        tags.add("测试");
        if (index % 3 == 0) tags.add("重点");
        if (index % 5 == 0) tags.add("难点");
        question.setTags(tags);

        // 设置复习信息
        if (includeReviewInfo) {
            QuestionItem.ReviewInfo reviewInfo = new QuestionItem.ReviewInfo();
            reviewInfo.setEaseFactor(2.5 + (index % 5) * 0.1); // 2.5-2.9
            reviewInfo.setInterval(index % 7 + 1); // 1-7天
            reviewInfo.setReviewCount(index % 10); // 0-9次
            reviewInfo.setCorrectCount(Math.max(0, reviewInfo.getReviewCount() - index % 3)); // 模拟正确次数
            
            // 设置复习时间
            LocalDateTime now = LocalDateTime.now();
            reviewInfo.setLastReviewTime(now.minusDays(index % 7));
            reviewInfo.setNextReviewTime(now.plusDays(reviewInfo.getInterval()));
            
            question.setReviewInfo(reviewInfo);
        }

        // 设置元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "mock");
        metadata.put("generated", true);
        metadata.put("index", index);
        metadata.put("createdAt", LocalDateTime.now().toString());
        question.setMetadata(metadata);

        return question;
    }

    /**
     * 根据索引获取随机题目类型
     */
    private String getRandomQuestionType(int index) {
        String[] types = {"text", "choice", "code"};
        return types[index % types.length];
    }

    /**
     * 根据索引获取随机难度
     */
    private int getRandomDifficulty(int index) {
        return (index % 5) + 1; // 1-5
    }

    /**
     * 安全地将对象转换为整数
     */
    private int convertToInteger(Object obj) throws NumberFormatException {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else {
            throw new NumberFormatException("无法将 " + obj.getClass().getSimpleName() + " 转换为整数");
        }
    }
} 