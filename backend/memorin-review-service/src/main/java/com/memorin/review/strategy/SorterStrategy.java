package com.memorin.review.strategy;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;

/**
 * 排序器策略接口
 * 定义题目排序的策略抽象
 * 参照原系统的排序器策略：random, by-review-time, by-difficulty, by-accuracy, smart等
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public interface SorterStrategy {

    /**
     * 策略名称
     * 用于策略注册和识别
     * 
     * @return 策略唯一标识符
     */
    String getStrategyName();

    /**
     * 策略描述
     * 用于用户界面展示和日志记录
     * 
     * @return 策略的中文描述
     */
    String getDescription();

    /**
     * 排序题目列表
     * 根据排序规则对题目进行排序
     * 
     * @param questions 输入的题目列表
     * @param params 排序参数，如order(asc/desc)等
     * @param userId 用户ID，用于个性化排序
     * @return 排序后的题目列表
     * @throws Exception 排序处理异常
     */
    List<QuestionItem> sortQuestions(List<QuestionItem> questions, Map<String, Object> params, String userId) throws Exception;

    /**
     * 验证参数
     * 检查排序器执行所需的参数是否完整和有效
     * 
     * @param params 排序参数
     * @throws IllegalArgumentException 参数验证失败
     */
    void validateParams(Map<String, Object> params) throws IllegalArgumentException;

    /**
     * 获取支持的参数列表
     * 返回该排序器支持的参数名称和类型
     * 
     * @return 参数名称到参数类型的映射
     */
    Map<String, Class<?>> getSupportedParams();

    /**
     * 是否为稳定排序
     * 稳定排序保证相等元素的相对位置不变
     * 
     * @return true如果是稳定排序，false否则
     */
    default boolean isStable() {
        return true;
    }

    /**
     * 是否支持随机化
     * 某些排序策略支持随机化处理
     * 
     * @return true如果支持随机化，false否则
     */
    default boolean supportsRandomization() {
        return false;
    }

    /**
     * 获取排序复杂度
     * 用于性能评估和优化选择
     * 
     * @return 时间复杂度描述，如"O(n log n)"
     */
    default String getTimeComplexity() {
        return "O(n log n)";
    }

    /**
     * 是否需要复习数据
     * 某些排序策略需要访问用户的复习历史数据
     * 
     * @return true如果需要复习数据，false否则
     */
    default boolean requiresReviewData() {
        return false;
    }

    /**
     * 是否支持部分排序
     * 部分排序只对前K个元素进行排序，提高性能
     * 
     * @return true如果支持部分排序，false否则
     */
    default boolean supportsPartialSorting() {
        return false;
    }

    /**
     * 部分排序题目列表
     * 只对前topK个元素进行排序，用于性能优化
     * 
     * @param questions 输入的题目列表
     * @param params 排序参数
     * @param userId 用户ID
     * @param topK 需要排序的前K个元素数量
     * @return 部分排序后的题目列表
     * @throws Exception 排序处理异常
     * @throws UnsupportedOperationException 如果不支持部分排序
     */
    default List<QuestionItem> sortQuestionsPartial(List<QuestionItem> questions, Map<String, Object> params, 
                                                   String userId, int topK) throws Exception {
        throw new UnsupportedOperationException("该排序器不支持部分排序");
    }

    /**
     * 获取比较器
     * 返回用于比较题目的Comparator对象
     * 
     * @param params 排序参数
     * @param userId 用户ID
     * @return 题目比较器
     * @throws Exception 比较器创建异常
     */
    default Comparator<QuestionItem> getComparator(Map<String, Object> params, String userId) throws Exception {
        throw new UnsupportedOperationException("该排序器不提供比较器");
    }

    /**
     * 是否支持流式排序
     * 流式排序适用于大数据量场景
     * 
     * @return true如果支持流式排序，false否则
     */
    default boolean supportsStreamSorting() {
        return false;
    }

    /**
     * 获取排序优先级权重
     * 用于多字段排序的权重分配
     * 
     * @return 权重映射，字段名到权重值
     */
    default Map<String, Double> getSortingWeights() {
        return Collections.emptyMap();
    }

    /**
     * 是否支持自适应排序
     * 自适应排序根据数据特征选择最优排序算法
     * 
     * @return true如果支持自适应排序，false否则
     */
    default boolean supportsAdaptiveSorting() {
        return false;
    }

    /**
     * 评估排序效果
     * 对排序结果进行质量评估
     * 
     * @param originalQuestions 原始题目列表
     * @param sortedQuestions 排序后题目列表
     * @param params 排序参数
     * @param userId 用户ID
     * @return 排序效果评分(0-100)
     */
    default double evaluateSortingQuality(List<QuestionItem> originalQuestions, List<QuestionItem> sortedQuestions,
                                         Map<String, Object> params, String userId) {
        return -1.0; // -1表示不支持评估
    }

    /**
     * 获取排序说明
     * 返回排序逻辑的详细说明，用于用户理解
     * 
     * @param params 排序参数
     * @return 排序说明文本
     */
    default String getSortingExplanation(Map<String, Object> params) {
        return "使用" + getDescription() + "进行排序";
    }
} 