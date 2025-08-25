package com.memorin.review.strategy;

import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import java.util.List;
import java.util.Map;

/**
 * 数据源策略接口
 * 定义获取基础题目数据的策略抽象
 * 参照原系统的数据源策略：all-knowledge, knowledge-base, knowledge-area, mistakes等
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public interface DataSourceStrategy {

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
     * 获取基础题目数据
     * 根据策略类型和参数获取符合条件的题目列表
     * 
     * @param params 策略参数，如baseId, areaId, knowledgeIds等
     * @param userId 用户ID，用于个性化数据获取
     * @return 题目列表
     * @throws Exception 数据获取异常
     */
    List<QuestionItem> getBaseQuestions(Map<String, Object> params, String userId) throws Exception;

    /**
     * 验证参数
     * 检查策略执行所需的参数是否完整和有效
     * 
     * @param params 策略参数
     * @throws IllegalArgumentException 参数验证失败
     */
    void validateParams(Map<String, Object> params) throws IllegalArgumentException;

    /**
     * 获取支持的参数列表
     * 返回该策略支持的参数名称和类型
     * 
     * @return 参数名称到参数类型的映射
     */
    Map<String, Class<?>> getSupportedParams();

    /**
     * 是否需要用户认证
     * 某些数据源可能需要用户身份验证才能访问
     * 
     * @return true如果需要用户认证，false否则
     */
    default boolean requiresAuthentication() {
        return false;
    }

    /**
     * 获取预估题目数量
     * 用于性能优化和进度显示
     * 
     * @param params 策略参数
     * @param userId 用户ID
     * @return 预估的题目数量，-1表示无法预估
     */
    default int getEstimatedQuestionCount(Map<String, Object> params, String userId) {
        return -1;
    }

    /**
     * 是否支持增量获取
     * 用于大数据量场景的分页或流式处理
     * 
     * @return true如果支持增量获取，false否则
     */
    default boolean supportsIncremental() {
        return false;
    }

    /**
     * 增量获取题目数据
     * 用于分页或流式获取大量数据
     * 
     * @param params 策略参数
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 题目列表
     * @throws Exception 数据获取异常
     * @throws UnsupportedOperationException 如果不支持增量获取
     */
    default List<QuestionItem> getQuestionsIncremental(Map<String, Object> params, String userId, 
                                                      int offset, int limit) throws Exception {
        throw new UnsupportedOperationException("该数据源策略不支持增量获取");
    }
} 