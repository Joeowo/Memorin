package com.memorin.review.repository;

import com.memorin.review.entity.ReviewSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 复习提交记录数据访问接口
 * 提供复习提交记录的增删改查和复杂查询功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Repository
public interface ReviewSubmissionRepository extends JpaRepository<ReviewSubmission, String> {

    /**
     * 根据会话ID查询所有提交记录
     * 按题目索引排序
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "ORDER BY r.questionIndex ASC")
    List<ReviewSubmission> findBySessionIdOrderByIndex(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID和题目索引查询提交记录
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "AND r.questionIndex = :questionIndex")
    Optional<ReviewSubmission> findBySessionIdAndQuestionIndex(@Param("sessionId") String sessionId, 
                                                              @Param("questionIndex") Integer questionIndex);

    /**
     * 根据知识点ID查询所有提交记录
     * 按创建时间倒序排列
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.knowledgePointId = :knowledgePointId " +
           "ORDER BY r.createdAt DESC")
    List<ReviewSubmission> findByKnowledgePointId(@Param("knowledgePointId") String knowledgePointId);

    /**
     * 根据会话ID统计提交统计信息
     */
    @Query("SELECT " +
           "COUNT(r) as totalSubmissions, " +
           "SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) as correctCount, " +
           "SUM(CASE WHEN r.isSkipped = true THEN 1 ELSE 0 END) as skippedCount, " +
           "AVG(r.timeSpentSeconds) as avgTimeSpent, " +
           "AVG(r.qualityRating) as avgQualityRating " +
           "FROM ReviewSubmission r WHERE r.sessionId = :sessionId")
    Object[] getSubmissionStatsBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据知识点ID统计历史表现
     */
    @Query("SELECT " +
           "COUNT(r) as totalAttempts, " +
           "SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) as correctCount, " +
           "AVG(r.timeSpentSeconds) as avgTimeSpent, " +
           "AVG(r.qualityRating) as avgQualityRating, " +
           "MIN(r.createdAt) as firstAttempt, " +
           "MAX(r.createdAt) as lastAttempt " +
           "FROM ReviewSubmission r WHERE r.knowledgePointId = :knowledgePointId")
    Object[] getKnowledgePointStats(@Param("knowledgePointId") String knowledgePointId);

    /**
     * 查询会话中的错误提交
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "AND r.isCorrect = false ORDER BY r.questionIndex ASC")
    List<ReviewSubmission> findWrongSubmissionsBySessionId(@Param("sessionId") String sessionId);

    /**
     * 查询会话中的跳过提交
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "AND r.isSkipped = true ORDER BY r.questionIndex ASC")
    List<ReviewSubmission> findSkippedSubmissionsBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据质量评分查询提交记录
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "AND r.qualityRating = :qualityRating ORDER BY r.questionIndex ASC")
    List<ReviewSubmission> findBySessionIdAndQualityRating(@Param("sessionId") String sessionId, 
                                                          @Param("qualityRating") Integer qualityRating);

    /**
     * 查询指定时间范围内的提交记录
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "AND r.createdAt BETWEEN :startTime AND :endTime " +
           "ORDER BY r.questionIndex ASC")
    List<ReviewSubmission> findBySessionIdAndTimeRange(@Param("sessionId") String sessionId,
                                                      @Param("startTime") Long startTime,
                                                      @Param("endTime") Long endTime);

    /**
     * 根据题目类型查询提交记录
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "AND r.questionType = :questionType ORDER BY r.questionIndex ASC")
    List<ReviewSubmission> findBySessionIdAndQuestionType(@Param("sessionId") String sessionId, 
                                                         @Param("questionType") String questionType);

    /**
     * 查询知识点的最近一次提交记录
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.knowledgePointId = :knowledgePointId " +
           "ORDER BY r.createdAt DESC")
    List<ReviewSubmission> findLatestByKnowledgePointId(@Param("knowledgePointId") String knowledgePointId, 
                                                       Pageable pageable);

    /**
     * 统计用户的总体表现
     */
    @Query("SELECT " +
           "COUNT(DISTINCT r.sessionId) as totalSessions, " +
           "COUNT(r) as totalSubmissions, " +
           "SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) as totalCorrect, " +
           "SUM(CASE WHEN r.isSkipped = true THEN 1 ELSE 0 END) as totalSkipped, " +
           "AVG(r.timeSpentSeconds) as avgTimeSpent, " +
           "AVG(r.qualityRating) as avgQualityRating " +
           "FROM ReviewSubmission r " +
           "INNER JOIN ReviewSession s ON r.sessionId = s.id " +
           "WHERE s.userId = :userId AND s.isActive = true")
    Object[] getUserOverallStats(@Param("userId") String userId);

    /**
     * 查询用户在指定时间范围内的提交记录
     */
    @Query("SELECT r FROM ReviewSubmission r " +
           "INNER JOIN ReviewSession s ON r.sessionId = s.id " +
           "WHERE s.userId = :userId AND s.isActive = true " +
           "AND r.createdAt BETWEEN :startTime AND :endTime " +
           "ORDER BY r.createdAt DESC")
    List<ReviewSubmission> findUserSubmissionsInTimeRange(@Param("userId") String userId,
                                                          @Param("startTime") Long startTime,
                                                          @Param("endTime") Long endTime);

    /**
     * 统计各题目类型的表现
     */
    @Query("SELECT " +
           "r.questionType, " +
           "COUNT(r) as totalCount, " +
           "SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) as correctCount, " +
           "AVG(r.timeSpentSeconds) as avgTimeSpent " +
           "FROM ReviewSubmission r " +
           "INNER JOIN ReviewSession s ON r.sessionId = s.id " +
           "WHERE s.userId = :userId AND s.isActive = true " +
           "GROUP BY r.questionType")
    List<Object[]> getQuestionTypeStatsForUser(@Param("userId") String userId);

    /**
     * 查询需要复习的知识点
     * 基于错误率和时间间隔，简化查询避免CAST函数
     */
    @Query("SELECT r.knowledgePointId, " +
           "COUNT(r) as totalAttempts, " +
           "SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) as correctCount, " +
           "MAX(r.createdAt) as lastAttempt " +
           "FROM ReviewSubmission r " +
           "INNER JOIN ReviewSession s ON r.sessionId = s.id " +
           "WHERE s.userId = :userId AND s.isActive = true " +
           "GROUP BY r.knowledgePointId " +
           "HAVING (SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) * 1.0 / COUNT(r)) < :accuracyThreshold " +
           "OR MAX(r.createdAt) < :timeThreshold " +
           "ORDER BY (SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) * 1.0 / COUNT(r)) ASC")
    List<Object[]> findKnowledgePointsNeedingReview(@Param("userId") String userId,
                                                    @Param("accuracyThreshold") Double accuracyThreshold,
                                                    @Param("timeThreshold") Long timeThreshold);

    /**
     * 查询用户的难题知识点
     * 基于平均用时和错误率，简化查询避免CAST函数
     */
    @Query("SELECT r.knowledgePointId, " +
           "COUNT(r) as totalAttempts, " +
           "SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) as correctCount, " +
           "AVG(r.timeSpentSeconds) as avgTimeSpent, " +
           "AVG(r.qualityRating) as avgQualityRating " +
           "FROM ReviewSubmission r " +
           "INNER JOIN ReviewSession s ON r.sessionId = s.id " +
           "WHERE s.userId = :userId AND s.isActive = true " +
           "GROUP BY r.knowledgePointId " +
           "HAVING COUNT(r) >= :minAttempts " +
           "AND (AVG(r.timeSpentSeconds) > :timeThreshold OR " +
           "     (SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) * 1.0 / COUNT(r)) < :accuracyThreshold) " +
           "ORDER BY AVG(r.timeSpentSeconds) DESC, " +
           "         (SUM(CASE WHEN r.isCorrect = true THEN 1 ELSE 0 END) * 1.0 / COUNT(r)) ASC")
    List<Object[]> findDifficultKnowledgePoints(@Param("userId") String userId,
                                               @Param("minAttempts") Integer minAttempts,
                                               @Param("timeThreshold") Double timeThreshold,
                                               @Param("accuracyThreshold") Double accuracyThreshold);

    /**
     * 分页查询会话的提交记录
     */
    @Query("SELECT r FROM ReviewSubmission r WHERE r.sessionId = :sessionId " +
           "ORDER BY r.questionIndex ASC")
    Page<ReviewSubmission> findBySessionIdWithPaging(@Param("sessionId") String sessionId, Pageable pageable);

    /**
     * 删除会话的所有提交记录
     */
    @Query("DELETE FROM ReviewSubmission r WHERE r.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);
} 