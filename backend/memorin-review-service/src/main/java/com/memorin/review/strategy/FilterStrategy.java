package com.memorin.review.strategy;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * 过滤器策略接口
 * 定义题目过滤的策略抽象
 * 参照原系统的过滤器策略：due-for-review, by-difficulty, by-accuracy, by-tags等
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public interface FilterStrategy {

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
     * 过滤题目列表
     * 根据过滤条件筛选符合要求的题目
     * 
     * @param questions 输入的题目列表
     * @param params 过滤参数，如minDifficulty, maxDifficulty, tags等
     * @param userId 用户ID，用于个性化过滤
     * @return 过滤后的题目列表
     * @throws Exception 过滤处理异常
     */
    List<QuestionItem> filterQuestions(List<QuestionItem> questions, Map<String, Object> params, String userId) throws Exception;

    /**
     * 验证参数
     * 检查过滤器执行所需的参数是否完整和有效
     * 
     * @param params 过滤参数
     * @throws IllegalArgumentException 参数验证失败
     */
    void validateParams(Map<String, Object> params) throws IllegalArgumentException;

    /**
     * 获取支持的参数列表
     * 返回该过滤器支持的参数名称和类型
     * 
     * @return 参数名称到参数类型的映射
     */
    Map<String, Class<?>> getSupportedParams();

    /**
     * 获取过滤器优先级
     * 当有多个过滤器时，按优先级顺序执行
     * 
     * @return 优先级数值，数值越小优先级越高
     */
    default int getPriority() {
        return 100;
    }

    /**
     * 是否支持预过滤
     * 预过滤可以在数据源层面减少数据传输量
     * 
     * @return true如果支持预过滤，false否则
     */
    default boolean supportsPreFiltering() {
        return false;
    }

    /**
     * 生成预过滤条件
     * 用于在数据库查询层面进行预过滤
     * 
     * @param params 过滤参数
     * @param userId 用户ID
     * @return 预过滤条件的SQL WHERE子句或其他查询条件
     * @throws UnsupportedOperationException 如果不支持预过滤
     */
    default String generatePreFilterCondition(Map<String, Object> params, String userId) {
        throw new UnsupportedOperationException("该过滤器不支持预过滤");
    }

    /**
     * 获取过滤预估
     * 预估过滤后的题目数量，用于性能优化
     * 
     * @param inputCount 输入题目数量
     * @param params 过滤参数
     * @param userId 用户ID
     * @return 预估的过滤后题目数量，-1表示无法预估
     */
    default int getEstimatedOutputCount(int inputCount, Map<String, Object> params, String userId) {
        return -1;
    }

    /**
     * 是否为排他性过滤器
     * 排他性过滤器与其他过滤器冲突，只能单独使用
     * 
     * @return true如果是排他性过滤器，false否则
     */
    default boolean isExclusive() {
        return false;
    }

    /**
     * 获取与其他过滤器的兼容性
     * 返回不兼容的过滤器名称列表
     * 
     * @return 不兼容的过滤器策略名称列表
     */
    default List<String> getIncompatibleFilters() {
        return Collections.emptyList();
    }

    /**
     * 是否需要复习数据
     * 某些过滤器需要访问用户的复习历史数据
     * 
     * @return true如果需要复习数据，false否则
     */
    default boolean requiresReviewData() {
        return false;
    }

    /**
     * 是否支持批量过滤
     * 批量过滤可以提高大数据量场景的性能
     * 
     * @return true如果支持批量过滤，false否则
     */
    default boolean supportsBatchFiltering() {
        return false;
    }

    /**
     * 批量过滤题目列表
     * 对大量题目进行高效的批量过滤
     * 
     * @param questionBatches 分批的题目列表
     * @param params 过滤参数
     * @param userId 用户ID
     * @return 过滤后的题目列表
     * @throws Exception 过滤处理异常
     * @throws UnsupportedOperationException 如果不支持批量过滤
     */
    default List<QuestionItem> filterQuestionsBatch(List<List<QuestionItem>> questionBatches, 
                                                   Map<String, Object> params, String userId) throws Exception {
        throw new UnsupportedOperationException("该过滤器不支持批量过滤");
    }
} 