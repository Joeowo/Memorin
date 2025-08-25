package com.memorin.review.service;

import com.memorin.review.entity.MistakeRecord;
import com.memorin.review.repository.MistakeRecordRepository;
import com.memorin.review.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 错题管理服务
 * 实现错题记录、查询、分析和解决功能
 * 对应原系统storage.js中的错题管理逻辑
 */
@Service
@Transactional
public class MistakeManagementService {

    private static final Logger logger = LoggerFactory.getLogger(MistakeManagementService.class);

    @Autowired
    private MistakeRecordRepository mistakeRecordRepository;

    @Autowired
    private IdGenerator idGenerator;

    // ======================== 核心业务方法 ========================

    /**
     * 添加或更新错题记录
     * 对应原系统的addMistake方法
     * 
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @param mistakeReason 错误原因
     * @param questionType 题目类型
     * @param difficultyLevel 难度等级
     * @return 错题记录
     */
    public MistakeRecord addOrUpdateMistake(String userId, String knowledgePointId, 
                                          String mistakeReason, String questionType, String difficultyLevel) {
        logger.info("添加错题记录: userId={}, knowledgePointId={}, reason={}", userId, knowledgePointId, mistakeReason);
        
        // 查找是否已存在该知识点的错题记录
        Optional<MistakeRecord> existingMistake = mistakeRecordRepository
                .findByUserIdAndKnowledgePointId(userId, knowledgePointId);
        
        MistakeRecord mistakeRecord;
        
        if (existingMistake.isPresent()) {
            // 更新现有错题记录
            mistakeRecord = existingMistake.get();
            mistakeRecord.incrementMistakeCount(mistakeReason);
            logger.info("更新现有错题记录: id={}, 新错误次数={}", mistakeRecord.getId(), mistakeRecord.getMistakeCount());
        } else {
            // 创建新错题记录
            String mistakeId = idGenerator.generateMistakeRecordId();
            mistakeRecord = new MistakeRecord(mistakeId, userId, knowledgePointId);
            mistakeRecord.setQuestionType(questionType);
            mistakeRecord.setDifficultyLevel(difficultyLevel);
            mistakeRecord.incrementMistakeCount(mistakeReason);
            logger.info("创建新错题记录: id={}", mistakeId);
        }
        
        // 保存错题记录
        mistakeRecord = mistakeRecordRepository.save(mistakeRecord);
        logger.info("错题记录保存成功: id={}", mistakeRecord.getId());
        
        return mistakeRecord;
    }

    /**
     * 记录复习结果（用于统计）
     * 
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @param isCorrect 是否正确
     */
    public void recordReviewResult(String userId, String knowledgePointId, boolean isCorrect) {
        logger.debug("记录复习结果: userId={}, knowledgePointId={}, isCorrect={}", userId, knowledgePointId, isCorrect);
        
        Optional<MistakeRecord> mistakeOpt = mistakeRecordRepository
                .findByUserIdAndKnowledgePointId(userId, knowledgePointId);
        
        if (mistakeOpt.isPresent()) {
            MistakeRecord mistake = mistakeOpt.get();
            mistake.incrementReviewCount(isCorrect);
            mistakeRecordRepository.save(mistake);
            logger.debug("更新错题统计: id={}, 总复习次数={}, 正确次数={}", 
                        mistake.getId(), mistake.getTotalReviewCount(), mistake.getCorrectCount());
        }
    }

    /**
     * 标记错题为已解决
     * 对应原系统的resolveMistake方法
     * 
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @return 是否成功
     */
    public boolean resolveMistake(String userId, String knowledgePointId) {
        logger.info("标记错题为已解决: userId={}, knowledgePointId={}", userId, knowledgePointId);
        
        Optional<MistakeRecord> mistakeOpt = mistakeRecordRepository
                .findByUserIdAndKnowledgePointId(userId, knowledgePointId);
        
        if (mistakeOpt.isPresent()) {
            MistakeRecord mistake = mistakeOpt.get();
            mistake.markAsResolved();
            mistakeRecordRepository.save(mistake);
            logger.info("错题已标记为解决: id={}", mistake.getId());
            return true;
        } else {
            logger.warn("未找到错题记录: userId={}, knowledgePointId={}", userId, knowledgePointId);
            return false;
        }
    }

    /**
     * 重新标记错题为未解决
     * 
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @return 是否成功
     */
    public boolean unresolveMistake(String userId, String knowledgePointId) {
        logger.info("重新标记错题为未解决: userId={}, knowledgePointId={}", userId, knowledgePointId);
        
        Optional<MistakeRecord> mistakeOpt = mistakeRecordRepository
                .findByUserIdAndKnowledgePointId(userId, knowledgePointId);
        
        if (mistakeOpt.isPresent()) {
            MistakeRecord mistake = mistakeOpt.get();
            mistake.markAsUnresolved();
            mistakeRecordRepository.save(mistake);
            logger.info("错题已重新标记为未解决: id={}", mistake.getId());
            return true;
        } else {
            logger.warn("未找到错题记录: userId={}, knowledgePointId={}", userId, knowledgePointId);
            return false;
        }
    }

    // ======================== 查询方法 ========================

    /**
     * 获取用户的所有错题
     * 对应原系统的getMistakes方法
     * 
     * @param userId 用户ID
     * @return 错题列表
     */
    public List<MistakeRecord> getUserMistakes(String userId) {
        logger.debug("获取用户所有错题: userId={}", userId);
        return mistakeRecordRepository.findByUserIdOrderByLastMistakeTimeDesc(userId);
    }

    /**
     * 获取用户的未解决错题
     * 对应原系统的all-mistakes策略
     * 
     * @param userId 用户ID
     * @return 未解决错题列表
     */
    public List<MistakeRecord> getUnresolvedMistakes(String userId) {
        logger.debug("获取用户未解决错题: userId={}", userId);
        return mistakeRecordRepository.findByUserIdAndIsResolvedOrderByLastMistakeTimeDesc(userId, false);
    }

    /**
     * 获取用户的已解决错题
     * 
     * @param userId 用户ID
     * @return 已解决错题列表
     */
    public List<MistakeRecord> getResolvedMistakes(String userId) {
        logger.debug("获取用户已解决错题: userId={}", userId);
        return mistakeRecordRepository.findByUserIdAndIsResolvedOrderByLastMistakeTimeDesc(userId, true);
    }

    /**
     * 根据知识点ID列表获取错题
     * 用于按知识库或知识区域查询错题
     * 
     * @param userId 用户ID
     * @param knowledgePointIds 知识点ID列表
     * @return 错题列表
     */
    public List<MistakeRecord> getMistakesByKnowledgePoints(String userId, List<String> knowledgePointIds) {
        logger.debug("按知识点查询错题: userId={}, 知识点数量={}", userId, knowledgePointIds.size());
        return mistakeRecordRepository.findByUserIdAndKnowledgePointIdIn(userId, knowledgePointIds);
    }

    /**
     * 获取需要重点复习的错题
     * 
     * @param userId 用户ID
     * @param threshold 错误次数阈值
     * @return 高优先级错题列表
     */
    public List<MistakeRecord> getHighPriorityMistakes(String userId, Integer threshold) {
        logger.debug("获取高优先级错题: userId={}, threshold={}", userId, threshold);
        return mistakeRecordRepository.findHighPriorityMistakes(userId, threshold);
    }

    /**
     * 获取最近N天的错题
     * 
     * @param userId 用户ID
     * @param days 天数
     * @return 最近错题列表
     */
    public List<MistakeRecord> getRecentMistakes(String userId, int days) {
        LocalDateTime sinceTime = LocalDateTime.now().minusDays(days);
        logger.debug("获取最近{}天错题: userId={}", days, userId);
        return mistakeRecordRepository.findRecentMistakes(userId, sinceTime);
    }

    // ======================== 统计方法 ========================

    /**
     * 获取用户错题统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息Map
     */
    public Map<String, Object> getMistakeStatistics(String userId) {
        logger.debug("获取错题统计: userId={}", userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // 基础统计
        stats.put("totalMistakes", mistakeRecordRepository.countTotalMistakes(userId));
        stats.put("unresolvedMistakes", mistakeRecordRepository.countUnresolvedMistakes(userId));
        stats.put("resolvedMistakes", mistakeRecordRepository.countResolvedMistakes(userId));
        
        // 平均统计
        stats.put("averageMistakeCount", mistakeRecordRepository.getAverageMistakeCount(userId));
        stats.put("averageAccuracyRate", mistakeRecordRepository.getAverageAccuracyRate(userId));
        
        // 最近统计
        stats.put("recentMistakes7Days", mistakeRecordRepository.countRecentMistakes(userId, LocalDateTime.now().minusDays(7)));
        stats.put("recentMistakes30Days", mistakeRecordRepository.countRecentMistakes(userId, LocalDateTime.now().minusDays(30)));
        stats.put("recentResolved7Days", mistakeRecordRepository.countRecentResolvedMistakes(userId, LocalDateTime.now().minusDays(7)));
        
        // 分类统计
        stats.put("mistakesByType", mistakeRecordRepository.countMistakesByQuestionType(userId));
        stats.put("mistakesByDifficulty", mistakeRecordRepository.countMistakesByDifficulty(userId));
        
        // 解决率
        Long total = (Long) stats.get("totalMistakes");
        Long resolved = (Long) stats.get("resolvedMistakes");
        double resolutionRate = total > 0 ? (double) resolved / total : 0.0;
        stats.put("resolutionRate", resolutionRate);
        
        logger.debug("错题统计完成: userId={}, 总错题数={}, 解决率={}", userId, total, resolutionRate);
        
        return stats;
    }

    /**
     * 获取按月统计的错题数据
     * 
     * @param userId 用户ID
     * @return 月度统计数据
     */
    public List<Object[]> getMistakeStatsByMonth(String userId) {
        logger.debug("获取月度错题统计: userId={}", userId);
        return mistakeRecordRepository.getMistakeStatsByMonth(userId);
    }

    // ======================== 删除方法 ========================

    /**
     * 删除错题记录
     * 
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @return 是否成功
     */
    public boolean deleteMistake(String userId, String knowledgePointId) {
        logger.info("删除错题记录: userId={}, knowledgePointId={}", userId, knowledgePointId);
        
        Optional<MistakeRecord> mistakeOpt = mistakeRecordRepository
                .findByUserIdAndKnowledgePointId(userId, knowledgePointId);
        
        if (mistakeOpt.isPresent()) {
            mistakeRecordRepository.delete(mistakeOpt.get());
            logger.info("错题记录已删除: id={}", mistakeOpt.get().getId());
            return true;
        } else {
            logger.warn("未找到要删除的错题记录: userId={}, knowledgePointId={}", userId, knowledgePointId);
            return false;
        }
    }

    /**
     * 清理已解决的错题记录
     * 
     * @param userId 用户ID
     */
    public void cleanupResolvedMistakes(String userId) {
        logger.info("清理已解决的错题记录: userId={}", userId);
        mistakeRecordRepository.deleteResolvedMistakes(userId);
        logger.info("已解决错题记录清理完成: userId={}", userId);
    }

    /**
     * 删除用户的所有错题记录
     * 
     * @param userId 用户ID
     */
    public void deleteAllUserMistakes(String userId) {
        logger.warn("删除用户所有错题记录: userId={}", userId);
        mistakeRecordRepository.deleteByUserId(userId);
        logger.warn("用户所有错题记录已删除: userId={}", userId);
    }

    // ======================== 辅助方法 ========================

    /**
     * 检查知识点是否有错题记录
     * 
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @return 是否存在错题记录
     */
    public boolean hasMistakeRecord(String userId, String knowledgePointId) {
        return mistakeRecordRepository.findByUserIdAndKnowledgePointId(userId, knowledgePointId).isPresent();
    }

    /**
     * 获取指定知识点的错题记录
     * 
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @return 错题记录（可能为空）
     */
    public Optional<MistakeRecord> getMistakeRecord(String userId, String knowledgePointId) {
        return mistakeRecordRepository.findByUserIdAndKnowledgePointId(userId, knowledgePointId);
    }
} 