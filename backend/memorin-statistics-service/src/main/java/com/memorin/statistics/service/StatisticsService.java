package com.memorin.statistics.service;

import java.util.Map;

/**
 * 统计分析服务接口
 * 定义学习统计、趋势分析、智能建议等核心业务方法
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public interface StatisticsService {
    
    /**
     * 获取用户学习概览统计
     * 
     * 计算指定时间范围内的用户学习统计数据，包括：
     * - 总学习时长 (totalLearningHours)
     * - 掌握知识点数量 (masteredKnowledgePoints)  
     * - 复习效果评估 (reviewEffectiveness)
     * - 错题改善率 (mistakeImprovementRate)
     * 
     * @param userId 用户ID，不能为空
     * @param days 统计天数，必须在1-365之间
     * @return 包含统计数据的Map
     *         - userId: 用户ID
     *         - analysisRange: 分析范围描述
     *         - totalLearningHours: 总学习时长(小时)
     *         - masteredKnowledgePoints: 掌握的知识点数量
     *         - reviewEffectiveness: 复习效果评分(0-1)
     *         - mistakeImprovementRate: 错题改善率(0-1)
     *         - lastUpdated: 最后更新时间
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException 当统计计算失败时抛出
     */
    Map<String, Object> getUserOverviewStatistics(String userId, int days);
} 