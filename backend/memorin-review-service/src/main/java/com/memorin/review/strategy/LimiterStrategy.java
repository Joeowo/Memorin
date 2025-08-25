package com.memorin.review.strategy;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import java.util.List;
import java.util.Map;

/**
 * 限制器策略接口
 * 定义题目数量限制的策略抽象
 * 参照原系统的限制器策略：fixed-count, percentage, time-limit, smart-limit等
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public interface LimiterStrategy {

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
     * 限制题目列表
     * 根据限制规则对题目进行数量限制
     * 
     * @param questions 输入的题目列表
     * @param params 限制参数，如count, percentage, timeLimit等
     * @param userId 用户ID，用于个性化限制
     * @return 限制后的题目列表
     * @throws Exception 限制处理异常
     */
    List<QuestionItem> limitQuestions(List<QuestionItem> questions, Map<String, Object> params, String userId) throws Exception;

    /**
     * 验证参数
     * 检查限制器执行所需的参数是否完整和有效
     * 
     * @param params 限制参数
     * @throws IllegalArgumentException 参数验证失败
     */
    void validateParams(Map<String, Object> params) throws IllegalArgumentException;

    /**
     * 获取支持的参数列表
     * 返回该限制器支持的参数名称和类型
     * 
     * @return 参数名称到参数类型的映射
     */
    Map<String, Class<?>> getSupportedParams();

    /**
     * 计算目标数量
     * 根据限制参数计算实际的题目数量
     * 
     * @param inputCount 输入题目数量
     * @param params 限制参数
     * @param userId 用户ID
     * @return 目标题目数量
     * @throws Exception 计算异常
     */
    int calculateTargetCount(int inputCount, Map<String, Object> params, String userId) throws Exception;

    /**
     * 是否为动态限制
     * 动态限制根据用户状态或系统状态调整限制数量
     * 
     * @return true如果是动态限制，false否则
     */
    default boolean isDynamic() {
        return false;
    }

    /**
     * 是否支持预计算
     * 预计算可以在处理前确定最终数量
     * 
     * @return true如果支持预计算，false否则
     */
    default boolean supportsPreCalculation() {
        return true;
    }

    /**
     * 获取限制优先级
     * 当有多个限制条件时，按优先级执行
     * 
     * @return 优先级数值，数值越小优先级越高
     */
    default int getPriority() {
        return 100;
    }

    /**
     * 是否需要用户历史数据
     * 某些智能限制策略需要用户的学习历史
     * 
     * @return true如果需要用户历史数据，false否则
     */
    default boolean requiresUserHistory() {
        return false;
    }

    /**
     * 是否支持分批限制
     * 分批限制可以处理大数据量场景
     * 
     * @return true如果支持分批限制，false否则
     */
    default boolean supportsBatchLimiting() {
        return false;
    }

    /**
     * 分批限制题目列表
     * 对大量题目进行分批处理和限制
     * 
     * @param questionBatches 分批的题目列表
     * @param params 限制参数
     * @param userId 用户ID
     * @return 限制后的题目列表
     * @throws Exception 限制处理异常
     * @throws UnsupportedOperationException 如果不支持分批限制
     */
    default List<QuestionItem> limitQuestionsBatch(List<List<QuestionItem>> questionBatches, 
                                                  Map<String, Object> params, String userId) throws Exception {
        throw new UnsupportedOperationException("该限制器不支持分批限制");
    }

    /**
     * 是否支持保留策略
     * 保留策略决定如何选择保留哪些题目
     * 
     * @return true如果支持保留策略，false否则
     */
    default boolean supportsRetentionStrategy() {
        return false;
    }

    /**
     * 应用保留策略
     * 智能选择保留哪些题目，而不是简单截取
     * 
     * @param questions 输入的题目列表
     * @param targetCount 目标数量
     * @param params 限制参数
     * @param userId 用户ID
     * @return 经过保留策略选择的题目列表
     * @throws Exception 保留策略处理异常
     * @throws UnsupportedOperationException 如果不支持保留策略
     */
    default List<QuestionItem> applyRetentionStrategy(List<QuestionItem> questions, int targetCount, 
                                                     Map<String, Object> params, String userId) throws Exception {
        throw new UnsupportedOperationException("该限制器不支持保留策略");
    }

    /**
     * 获取限制效率
     * 评估限制策略的效率和质量
     * 
     * @param originalCount 原始题目数量
     * @param limitedCount 限制后题目数量
     * @param params 限制参数
     * @param userId 用户ID
     * @return 效率评分(0-100)
     */
    default double getLimitingEfficiency(int originalCount, int limitedCount, 
                                        Map<String, Object> params, String userId) {
        if (originalCount <= 0) return 0.0;
        return (double) limitedCount / originalCount * 100.0;
    }

    /**
     * 是否支持渐进式限制
     * 渐进式限制根据用户能力逐步调整限制数量
     * 
     * @return true如果支持渐进式限制，false否则
     */
    default boolean supportsProgressiveLimiting() {
        return false;
    }

    /**
     * 应用渐进式限制
     * 根据用户能力和学习进度动态调整限制数量
     * 
     * @param questions 输入的题目列表
     * @param params 限制参数
     * @param userId 用户ID
     * @param userLevel 用户能力等级(1-10)
     * @return 渐进式限制后的题目列表
     * @throws Exception 渐进式限制处理异常
     * @throws UnsupportedOperationException 如果不支持渐进式限制
     */
    default List<QuestionItem> applyProgressiveLimiting(List<QuestionItem> questions, Map<String, Object> params, 
                                                       String userId, int userLevel) throws Exception {
        throw new UnsupportedOperationException("该限制器不支持渐进式限制");
    }

    /**
     * 获取建议限制数量
     * 基于用户状态和题目特征给出建议的限制数量
     * 
     * @param questions 输入的题目列表
     * @param params 限制参数
     * @param userId 用户ID
     * @return 建议的限制数量，-1表示无建议
     */
    default int getSuggestedLimit(List<QuestionItem> questions, Map<String, Object> params, String userId) {
        return -1;
    }

    /**
     * 获取限制解释
     * 返回限制逻辑的详细说明，用于用户理解
     * 
     * @param params 限制参数
     * @param resultCount 实际限制结果数量
     * @return 限制解释文本
     */
    default String getLimitingExplanation(Map<String, Object> params, int resultCount) {
        return "使用" + getDescription() + "限制为" + resultCount + "道题目";
    }

    /**
     * 是否支持最小保证数量
     * 确保即使在严格限制下也有最小数量的题目
     * 
     * @return true如果支持最小保证数量，false否则
     */
    default boolean supportsMinimumGuarantee() {
        return false;
    }

    /**
     * 获取最小保证数量
     * 返回在任何限制条件下都应该保证的最小题目数量
     * 
     * @param params 限制参数
     * @param userId 用户ID
     * @return 最小保证数量
     */
    default int getMinimumGuarantee(Map<String, Object> params, String userId) {
        return 1;
    }
} 