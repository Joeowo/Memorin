package com.memorin.review.strategy.impl;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import com.memorin.review.strategy.LimiterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * 固定数量限制器策略实现
 * 参照原系统的 fixed-count 限制器
 * 简单截取指定数量的题目
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class FixedCountLimiterStrategy implements LimiterStrategy {

    private static final Logger log = LoggerFactory.getLogger(FixedCountLimiterStrategy.class);

    @Override
    public String getStrategyName() {
        return "fixed-count";
    }

    @Override
    public String getDescription() {
        return "固定数量限制";
    }

    @Override
    public List<QuestionItem> limitQuestions(List<QuestionItem> questions, Map<String, Object> params, String userId) throws Exception {
        if (questions == null || questions.isEmpty()) {
            log.debug("[固定数量限制] 输入题目列表为空，返回空列表");
            return questions;
        }

        int targetCount = calculateTargetCount(questions.size(), params, userId);
        
        if (targetCount <= 0) {
            log.warn("[固定数量限制] 目标数量 {} 无效，返回空列表", targetCount);
            return Collections.emptyList();
        }

        if (targetCount >= questions.size()) {
            log.debug("[固定数量限制] 目标数量 {} 大于等于输入数量 {}，返回全部题目", targetCount, questions.size());
            return questions;
        }

        List<QuestionItem> result = questions.subList(0, targetCount);
        
        log.debug("[固定数量限制] 从 {} 道题目中限制为 {} 道题目", questions.size(), result.size());
        
        return result;
    }

    @Override
    public void validateParams(Map<String, Object> params) throws IllegalArgumentException {
        if (params == null) {
            throw new IllegalArgumentException("固定数量限制器参数不能为空");
        }

        Object countObj = params.get("count");
        if (countObj == null) {
            throw new IllegalArgumentException("固定数量限制器缺少 'count' 参数");
        }

        try {
            int count = convertToInteger(countObj);
            if (count <= 0) {
                throw new IllegalArgumentException("count 参数必须大于0，当前值: " + count);
            }
            if (count > 1000) {
                throw new IllegalArgumentException("count 参数不能超过1000，当前值: " + count);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("count 参数必须是有效的整数，当前值: " + countObj);
        }
    }

    @Override
    public Map<String, Class<?>> getSupportedParams() {
        Map<String, Class<?>> params = new HashMap<>();
        params.put("count", Integer.class);
        return params;
    }

    @Override
    public int calculateTargetCount(int inputCount, Map<String, Object> params, String userId) throws Exception {
        Object countObj = params.get("count");
        if (countObj == null) {
            return inputCount; // 如果没有指定count，返回原数量
        }

        try {
            int count = convertToInteger(countObj);
            return Math.min(count, inputCount); // 不能超过输入数量
        } catch (NumberFormatException e) {
            log.warn("[固定数量限制] count 参数转换失败: {}, 使用输入数量: {}", countObj, inputCount);
            return inputCount;
        }
    }

    @Override
    public boolean supportsPreCalculation() {
        return true;
    }

    @Override
    public int getPriority() {
        return 50; // 中等优先级
    }

    @Override
    public boolean requiresUserHistory() {
        return false;
    }

    @Override
    public double getLimitingEfficiency(int originalCount, int limitedCount, Map<String, Object> params, String userId) {
        if (originalCount <= 0) return 0.0;
        return (double) limitedCount / originalCount * 100.0;
    }

    @Override
    public boolean supportsMinimumGuarantee() {
        return true;
    }

    @Override
    public int getMinimumGuarantee(Map<String, Object> params, String userId) {
        // 固定数量限制器总是保证至少返回1道题目（如果有的话）
        return 1;
    }

    @Override
    public String getLimitingExplanation(Map<String, Object> params, int resultCount) {
        Object countObj = params.get("count");
        if (countObj != null) {
            try {
                int requestedCount = convertToInteger(countObj);
                return String.format("限制为 %d 道题目（请求 %d 道）", resultCount, requestedCount);
            } catch (NumberFormatException e) {
                // 忽略转换错误
            }
        }
        return String.format("固定数量限制为 %d 道题目", resultCount);
    }

    /**
     * 安全地将对象转换为整数
     * 处理各种可能的数据类型
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