package com.memorin.review.strategy.impl;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import com.memorin.review.strategy.SorterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 随机排序器策略实现
 * 参照原系统的 random 排序器
 * 使用打乱算法对题目进行随机排序
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class RandomSorterStrategy implements SorterStrategy {

    private static final Logger log = LoggerFactory.getLogger(RandomSorterStrategy.class);

    @Override
    public String getStrategyName() {
        return "random";
    }

    @Override
    public String getDescription() {
        return "随机排序";
    }

    @Override
    public List<QuestionItem> sortQuestions(List<QuestionItem> questions, Map<String, Object> params, String userId) throws Exception {
        if (questions == null || questions.isEmpty()) {
            log.debug("[随机排序] 输入题目列表为空，返回空列表");
            return questions;
        }

        if (questions.size() == 1) {
            log.debug("[随机排序] 只有1道题目，无需排序");
            return questions;
        }

        // 创建副本避免修改原列表
        List<QuestionItem> result = new ArrayList<>(questions);
        
        // 获取随机种子（如果指定）
        Random random = createRandom(params);
        
        // 使用 Fisher-Yates 洗牌算法
        shuffleList(result, random);
        
        log.debug("[随机排序] 完成 {} 道题目的随机排序", result.size());
        
        return result;
    }

    @Override
    public void validateParams(Map<String, Object> params) throws IllegalArgumentException {
        if (params == null) {
            return; // 随机排序器可以没有参数
        }

        // 验证随机种子参数（如果存在）
        Object seedObj = params.get("seed");
        if (seedObj != null) {
            try {
                convertToLong(seedObj);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("seed 参数必须是有效的整数，当前值: " + seedObj);
            }
        }

        // 验证是否启用稳定排序（实际上随机排序不支持稳定性）
        Object stableObj = params.get("stable");
        if (stableObj != null && Boolean.TRUE.equals(stableObj)) {
            log.warn("[随机排序] 随机排序不支持稳定排序，将忽略 stable 参数");
        }
    }

    @Override
    public Map<String, Class<?>> getSupportedParams() {
        Map<String, Class<?>> params = new HashMap<>();
        params.put("seed", Long.class);    // 随机种子，用于可重现的随机排序
        params.put("stable", Boolean.class); // 稳定性标志（虽然随机排序不支持）
        return params;
    }

    @Override
    public boolean isStable() {
        return false; // 随机排序不是稳定排序
    }

    @Override
    public boolean supportsRandomization() {
        return true; // 这就是随机化排序
    }

    @Override
    public String getTimeComplexity() {
        return "O(n)"; // Fisher-Yates 洗牌算法的时间复杂度
    }

    @Override
    public boolean requiresReviewData() {
        return false; // 随机排序不需要复习数据
    }

    @Override
    public boolean supportsPartialSorting() {
        return false; // 随机排序没有部分排序的概念
    }

    @Override
    public Comparator<QuestionItem> getComparator(Map<String, Object> params, String userId) throws Exception {
        throw new UnsupportedOperationException("随机排序不提供比较器，因为它不基于比较");
    }

    @Override
    public boolean supportsStreamSorting() {
        return false; // 随机排序需要访问整个列表
    }

    @Override
    public Map<String, Double> getSortingWeights() {
        return Collections.emptyMap(); // 随机排序没有权重概念
    }

    @Override
    public boolean supportsAdaptiveSorting() {
        return false; // 随机排序不是自适应的
    }

    @Override
    public double evaluateSortingQuality(List<QuestionItem> originalQuestions, List<QuestionItem> sortedQuestions,
                                       Map<String, Object> params, String userId) {
        if (originalQuestions == null || sortedQuestions == null) {
            return 0.0;
        }

        if (originalQuestions.size() != sortedQuestions.size()) {
            return 0.0; // 如果数量不匹配，质量为0
        }

        if (originalQuestions.size() <= 1) {
            return 100.0; // 如果只有1个或0个元素，质量为100%
        }

        // 计算随机性质量：检查有多少位置发生了变化
        int changedPositions = 0;
        for (int i = 0; i < originalQuestions.size(); i++) {
            if (!originalQuestions.get(i).equals(sortedQuestions.get(i))) {
                changedPositions++;
            }
        }

        // 随机排序的质量基于位置变化的比例
        double changeRatio = (double) changedPositions / originalQuestions.size();
        return Math.min(100.0, changeRatio * 100.0);
    }

    @Override
    public String getSortingExplanation(Map<String, Object> params) {
        Object seedObj = params != null ? params.get("seed") : null;
        if (seedObj != null) {
            return "使用种子 " + seedObj + " 进行随机排序（可重现）";
        } else {
            return "使用完全随机的方式重新排列题目顺序";
        }
    }

    /**
     * 创建随机数生成器
     * 如果指定了种子，使用种子创建；否则使用系统时间
     */
    private Random createRandom(Map<String, Object> params) {
        if (params == null) {
            return new Random();
        }

        Object seedObj = params.get("seed");
        if (seedObj != null) {
            try {
                long seed = convertToLong(seedObj);
                log.debug("[随机排序] 使用指定种子: {}", seed);
                return new Random(seed);
            } catch (NumberFormatException e) {
                log.warn("[随机排序] 种子参数无效: {}, 使用系统时间", seedObj);
            }
        }

        return new Random();
    }

    /**
     * 使用 Fisher-Yates 洗牌算法打乱列表
     * 这是一个标准的、无偏的洗牌算法
     */
    private void shuffleList(List<QuestionItem> list, Random random) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // 交换元素
            QuestionItem temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    /**
     * 安全地将对象转换为长整数
     */
    private long convertToLong(Object obj) throws NumberFormatException {
        if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof String) {
            return Long.parseLong((String) obj);
        } else {
            throw new NumberFormatException("无法将 " + obj.getClass().getSimpleName() + " 转换为长整数");
        }
    }
} 