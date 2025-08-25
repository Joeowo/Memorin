package com.memorin.review.service;

import com.memorin.review.dto.request.ReviewAlgorithmRequest;
import com.memorin.review.dto.response.ReviewAlgorithmResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SM-2间隔重复算法服务
 * 实现改进的SM-2算法，计算下次复习时间和易度因子调整
 * 算法逻辑与原系统review.js保持完全一致
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@Transactional
public class ReviewAlgorithmService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewAlgorithmService.class);

    // 算法常量
    private static final double MIN_EASE_FACTOR = 1.3;
    private static final double MAX_EASE_FACTOR = 3.0;
    private static final double EASE_FACTOR_PENALTY_ERROR = 0.3;      // 错误时的惩罚
    private static final double EASE_FACTOR_PENALTY_VAGUE = 0.1;      // 模糊时的惩罚
    private static final double EASE_FACTOR_REWARD = 0.15;            // 正确时的奖励
    private static final double INTERVAL_REDUCTION_FACTOR = 0.6;      // 模糊时间隔缩减系数
    private static final long ERROR_REVIEW_DELAY_HOURS = 6;           // 错误后快速复习延迟(小时)
    private static final long HOURS_TO_MILLISECONDS = 60 * 60 * 1000; // 小时转毫秒
    private static final long DAYS_TO_MILLISECONDS = 24 * HOURS_TO_MILLISECONDS; // 天转毫秒

    /**
     * 计算下次复习时间 - 改进的SM-2算法
     * 与原系统review.js中的calculateNextReview方法逻辑完全一致
     * 
     * @param request 算法计算请求，包含easeFactor、interval、quality
     * @return 计算结果，包含新的易度因子、间隔和下次复习时间
     */
    public ReviewAlgorithmResponse calculateNextReview(ReviewAlgorithmRequest request) {
        logger.info("开始计算SM-2算法，输入参数: easeFactor={}, interval={}, quality={}", 
                   request.getEaseFactor(), request.getInterval(), request.getQuality());

        Double currentEaseFactor = request.getEaseFactor();
        Integer currentInterval = request.getInterval();
        Integer quality = request.getQuality();
        long currentTime = System.currentTimeMillis();

        Double newEaseFactor;
        Integer newInterval;
        Long nextReviewTime;

        // 根据质量评分应用不同的算法策略
        switch (quality) {
            case 1:
                // 自评错误：大幅降低熟悉因子，立即重新复习
                newEaseFactor = Math.max(MIN_EASE_FACTOR, currentEaseFactor - EASE_FACTOR_PENALTY_ERROR);
                newInterval = 1;
                nextReviewTime = currentTime + ERROR_REVIEW_DELAY_HOURS * HOURS_TO_MILLISECONDS; // 6小时后复习
                
                logger.info("错误评分处理: 新易度因子={}, 新间隔={}天, 6小时后复习", newEaseFactor, newInterval);
                break;

            case 2:
                // 自评模糊：轻微降低熟悉因子，短期内复习
                newEaseFactor = Math.max(MIN_EASE_FACTOR, currentEaseFactor - EASE_FACTOR_PENALTY_VAGUE);
                newInterval = Math.max(1, (int) Math.round(currentInterval * INTERVAL_REDUCTION_FACTOR)); // 缩短间隔
                nextReviewTime = currentTime + newInterval * DAYS_TO_MILLISECONDS;
                
                logger.info("模糊评分处理: 新易度因子={}, 新间隔={}天, {}天后复习", newEaseFactor, newInterval, newInterval);
                break;

            case 3:
                // 自评正确：提高熟悉因子，按正常间隔复习
                newEaseFactor = Math.min(MAX_EASE_FACTOR, currentEaseFactor + EASE_FACTOR_REWARD); // 奖励，但有上限
                
                // 间隔计算逻辑与原系统完全一致
                if (currentInterval == 1) {
                    newInterval = 3; // 第一次正确后3天复习
                } else if (currentInterval < 6) {
                    newInterval = 6; // 短期内正确后6天复习
                } else {
                    newInterval = (int) Math.round(currentInterval * newEaseFactor);
                }
                
                nextReviewTime = currentTime + newInterval * DAYS_TO_MILLISECONDS;
                
                logger.info("正确评分处理: 新易度因子={}, 新间隔={}天, {}天后复习", newEaseFactor, newInterval, newInterval);
                break;

            default:
                logger.error("无效的质量评分: {}", quality);
                throw new IllegalArgumentException("质量评分必须是1、2或3");
        }

        // 创建响应对象
        ReviewAlgorithmResponse response = new ReviewAlgorithmResponse(newEaseFactor, newInterval, nextReviewTime);
        
        logger.info("SM-2算法计算完成: 输入[EF={}, I={}, Q={}] -> 输出[EF={}, I={}, 复习强度={}]", 
                   currentEaseFactor, currentInterval, quality, 
                   newEaseFactor, newInterval, response.getReviewIntensity());

        return response;
    }

    /**
     * 批量计算多个知识点的复习时间
     * 用于复习会话中的批量处理
     * 
     * @param requests 多个算法计算请求
     * @return 对应的计算结果列表
     */
    public java.util.List<ReviewAlgorithmResponse> batchCalculateNextReview(
            java.util.List<ReviewAlgorithmRequest> requests) {
        
        logger.info("开始批量计算SM-2算法，处理{}个知识点", requests.size());
        
        return requests.stream()
                .map(this::calculateNextReview)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取默认的算法参数
     * 用于新知识点的初始化
     * 
     * @return 默认算法参数
     */
    public ReviewAlgorithmResponse getDefaultParameters() {
        double defaultEaseFactor = 2.5; // 默认易度因子
        int defaultInterval = 1;        // 默认间隔1天
        long nextReviewTime = System.currentTimeMillis() + DAYS_TO_MILLISECONDS;
        
        ReviewAlgorithmResponse response = new ReviewAlgorithmResponse(
            defaultEaseFactor, defaultInterval, nextReviewTime);
        response.setRecommendation("新知识点，建议明天开始第一次复习");
        
        logger.info("返回默认算法参数: EF={}, I={}天", defaultEaseFactor, defaultInterval);
        return response;
    }

    /**
     * 验证算法参数的有效性
     * 确保输入参数在合理范围内
     * 
     * @param easeFactor 易度因子
     * @param interval 复习间隔
     * @param quality 质量评分
     * @throws IllegalArgumentException 参数无效时抛出异常
     */
    public void validateParameters(Double easeFactor, Integer interval, Integer quality) {
        if (easeFactor == null || easeFactor < MIN_EASE_FACTOR || easeFactor > MAX_EASE_FACTOR) {
            throw new IllegalArgumentException(
                String.format("易度因子必须在%.1f到%.1f之间", MIN_EASE_FACTOR, MAX_EASE_FACTOR));
        }
        
        if (interval == null || interval < 1 || interval > 365) {
            throw new IllegalArgumentException("复习间隔必须在1到365天之间");
        }
        
        if (quality == null || quality < 1 || quality > 3) {
            throw new IllegalArgumentException("质量评分必须是1（错误）、2（模糊）或3（正确）");
        }
        
        logger.debug("参数验证通过: EF={}, I={}, Q={}", easeFactor, interval, quality);
    }

    /**
     * 计算掌握度评估
     * 基于易度因子和复习历史评估知识点掌握程度
     * 
     * @param easeFactor 当前易度因子
     * @param reviewCount 复习次数
     * @param correctCount 正确次数
     * @return 掌握度评估（0.0-1.0）
     */
    public Double calculateMasteryLevel(Double easeFactor, Integer reviewCount, Integer correctCount) {
        if (reviewCount == null || reviewCount == 0) {
            return 0.0;
        }
        
        double accuracy = correctCount != null ? (double) correctCount / reviewCount : 0.0;
        double easeContribution = (easeFactor - MIN_EASE_FACTOR) / (MAX_EASE_FACTOR - MIN_EASE_FACTOR);
        double masteryLevel = (accuracy * 0.7) + (easeContribution * 0.3);
        
        logger.debug("掌握度计算: 准确率={}, 易度贡献={}, 最终掌握度={}", 
                    accuracy, easeContribution, masteryLevel);
        
        return Math.min(1.0, Math.max(0.0, masteryLevel));
    }
} 