package com.memorin.review.repository;

import com.memorin.review.entity.MistakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 错题记录Repository
 * 对应原系统storage.js中的错题管理功能
 */
@Repository
public interface MistakeRecordRepository extends JpaRepository<MistakeRecord, String> {
    
    // ======================== 基础查询 ========================
    
    /**
     * 根据用户ID和知识点ID查找错题记录
     */
    Optional<MistakeRecord> findByUserIdAndKnowledgePointId(String userId, String knowledgePointId);
    
    /**
     * 查找用户的所有错题记录
     */
    List<MistakeRecord> findByUserIdOrderByLastMistakeTimeDesc(String userId);
    
    /**
     * 查找用户的未解决错题
     */
    List<MistakeRecord> findByUserIdAndIsResolvedOrderByLastMistakeTimeDesc(String userId, Boolean isResolved);
    
    /**
     * 查找用户指定知识点的错题记录
     */
    List<MistakeRecord> findByUserIdAndKnowledgePointIdIn(String userId, List<String> knowledgePointIds);
    
    // ======================== 条件查询 ========================
    
    /**
     * 根据错误次数范围查询错题
     */
    @Query("SELECT m FROM MistakeRecord m WHERE m.userId = :userId AND m.mistakeCount BETWEEN :minCount AND :maxCount ORDER BY m.lastMistakeTime DESC")
    List<MistakeRecord> findByUserIdAndMistakeCountRange(@Param("userId") String userId, 
                                                         @Param("minCount") Integer minCount, 
                                                         @Param("maxCount") Integer maxCount);
    
    /**
     * 根据时间范围查询错题
     */
    @Query("SELECT m FROM MistakeRecord m WHERE m.userId = :userId AND m.lastMistakeTime BETWEEN :startTime AND :endTime ORDER BY m.lastMistakeTime DESC")
    List<MistakeRecord> findByUserIdAndTimeRange(@Param("userId") String userId, 
                                                 @Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据题目类型查询错题
     */
    List<MistakeRecord> findByUserIdAndQuestionTypeOrderByLastMistakeTimeDesc(String userId, String questionType);
    
    /**
     * 根据难度等级查询错题
     */
    List<MistakeRecord> findByUserIdAndDifficultyLevelOrderByLastMistakeTimeDesc(String userId, String difficultyLevel);
    
    /**
     * 查询最近N天的错题
     */
    @Query("SELECT m FROM MistakeRecord m WHERE m.userId = :userId AND m.lastMistakeTime >= :sinceTime ORDER BY m.lastMistakeTime DESC")
    List<MistakeRecord> findRecentMistakes(@Param("userId") String userId, @Param("sinceTime") LocalDateTime sinceTime);
    
    /**
     * 查询需要重点复习的错题（错误次数较多且未解决）
     */
    @Query("SELECT m FROM MistakeRecord m WHERE m.userId = :userId AND m.isResolved = false AND m.mistakeCount >= :threshold ORDER BY m.mistakeCount DESC, m.lastMistakeTime DESC")
    List<MistakeRecord> findHighPriorityMistakes(@Param("userId") String userId, @Param("threshold") Integer threshold);
    
    // ======================== 统计查询 ========================
    
    /**
     * 统计用户的总错题数
     */
    @Query("SELECT COUNT(m) FROM MistakeRecord m WHERE m.userId = :userId")
    Long countTotalMistakes(@Param("userId") String userId);
    
    /**
     * 统计用户的未解决错题数
     */
    @Query("SELECT COUNT(m) FROM MistakeRecord m WHERE m.userId = :userId AND m.isResolved = false")
    Long countUnresolvedMistakes(@Param("userId") String userId);
    
    /**
     * 统计用户的已解决错题数
     */
    @Query("SELECT COUNT(m) FROM MistakeRecord m WHERE m.userId = :userId AND m.isResolved = true")
    Long countResolvedMistakes(@Param("userId") String userId);
    
    /**
     * 统计用户各题目类型的错题分布
     */
    @Query("SELECT m.questionType, COUNT(m) FROM MistakeRecord m WHERE m.userId = :userId GROUP BY m.questionType")
    List<Object[]> countMistakesByQuestionType(@Param("userId") String userId);
    
    /**
     * 统计用户各难度等级的错题分布
     */
    @Query("SELECT m.difficultyLevel, COUNT(m) FROM MistakeRecord m WHERE m.userId = :userId GROUP BY m.difficultyLevel")
    List<Object[]> countMistakesByDifficulty(@Param("userId") String userId);
    
    /**
     * 计算用户的平均错误次数
     */
    @Query("SELECT AVG(m.mistakeCount) FROM MistakeRecord m WHERE m.userId = :userId")
    Double getAverageMistakeCount(@Param("userId") String userId);
    
    /**
     * 计算用户的整体正确率
     */
    @Query("SELECT AVG(m.correctCount * 1.0 / m.totalReviewCount) FROM MistakeRecord m WHERE m.userId = :userId AND m.totalReviewCount > 0")
    Double getAverageAccuracyRate(@Param("userId") String userId);
    
    // ======================== 时间统计 ========================
    
    /**
     * 统计最近N天的错题数量
     */
    @Query("SELECT COUNT(m) FROM MistakeRecord m WHERE m.userId = :userId AND m.lastMistakeTime >= :sinceTime")
    Long countRecentMistakes(@Param("userId") String userId, @Param("sinceTime") LocalDateTime sinceTime);
    
    /**
     * 统计最近N天的解决错题数量
     */
    @Query("SELECT COUNT(m) FROM MistakeRecord m WHERE m.userId = :userId AND m.resolvedTime >= :sinceTime AND m.isResolved = true")
    Long countRecentResolvedMistakes(@Param("userId") String userId, @Param("sinceTime") LocalDateTime sinceTime);
    
    /**
     * 按月统计错题数量
     */
    @Query("SELECT FUNCTION('YEAR', m.firstMistakeTime), FUNCTION('MONTH', m.firstMistakeTime), COUNT(m) " +
           "FROM MistakeRecord m WHERE m.userId = :userId " +
           "GROUP BY FUNCTION('YEAR', m.firstMistakeTime), FUNCTION('MONTH', m.firstMistakeTime) " +
           "ORDER BY FUNCTION('YEAR', m.firstMistakeTime) DESC, FUNCTION('MONTH', m.firstMistakeTime) DESC")
    List<Object[]> getMistakeStatsByMonth(@Param("userId") String userId);
    
    // ======================== 删除操作 ========================
    
    /**
     * 删除用户的所有错题记录
     */
    void deleteByUserId(String userId);
    
    /**
     * 删除用户指定知识点的错题记录
     */
    void deleteByUserIdAndKnowledgePointId(String userId, String knowledgePointId);
    
    /**
     * 删除已解决的错题记录
     */
    @Query("DELETE FROM MistakeRecord m WHERE m.userId = :userId AND m.isResolved = true")
    void deleteResolvedMistakes(@Param("userId") String userId);
} 