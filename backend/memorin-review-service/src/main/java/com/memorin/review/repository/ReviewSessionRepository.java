package com.memorin.review.repository;

import com.memorin.review.entity.ReviewSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 复习会话数据访问接口
 * 提供复习会话的增删改查和复杂查询功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Repository
public interface ReviewSessionRepository extends JpaRepository<ReviewSession, String> {

    /**
     * 根据用户ID查询所有激活的会话
     * 按创建时间倒序排列
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.userId = :userId AND s.isActive = true " +
           "ORDER BY s.createdAt DESC")
    List<ReviewSession> findByUserIdAndIsActiveTrue(@Param("userId") String userId);

    /**
     * 根据用户ID和状态查询会话
     * 按创建时间倒序排列
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.userId = :userId AND s.status = :status AND s.isActive = true " +
           "ORDER BY s.createdAt DESC")
    List<ReviewSession> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    /**
     * 根据用户ID查询正在进行的会话(ACTIVE或PAUSED状态)
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.userId = :userId " +
           "AND s.status IN ('ACTIVE', 'PAUSED') AND s.isActive = true " +
           "ORDER BY s.updatedAt DESC")
    List<ReviewSession> findActiveSessionsByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和复习模式查询会话
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.userId = :userId AND s.reviewMode = :reviewMode " +
           "AND s.isActive = true ORDER BY s.createdAt DESC")
    List<ReviewSession> findByUserIdAndReviewMode(@Param("userId") String userId, @Param("reviewMode") String reviewMode);

    /**
     * 查询用户在指定时间范围内的会话
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.userId = :userId " +
           "AND s.createdAt BETWEEN :startTime AND :endTime AND s.isActive = true " +
           "ORDER BY s.createdAt DESC")
    List<ReviewSession> findByUserIdAndTimeRange(@Param("userId") String userId, 
                                                @Param("startTime") Long startTime, 
                                                @Param("endTime") Long endTime);

    /**
     * 分页查询用户的会话历史
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.userId = :userId AND s.isActive = true " +
           "ORDER BY s.createdAt DESC")
    Page<ReviewSession> findByUserIdWithPaging(@Param("userId") String userId, Pageable pageable);

    /**
     * 根据用户ID和会话ID查询特定会话
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.id = :sessionId AND s.userId = :userId AND s.isActive = true")
    Optional<ReviewSession> findByIdAndUserId(@Param("sessionId") String sessionId, @Param("userId") String userId);

    /**
     * 查询已完成的会话数量
     */
    @Query("SELECT COUNT(s) FROM ReviewSession s WHERE s.userId = :userId " +
           "AND s.status = 'COMPLETED' AND s.isActive = true")
    Long countCompletedSessionsByUserId(@Param("userId") String userId);

    /**
     * 查询用户的会话统计信息
     */
    @Query("SELECT " +
           "COUNT(s) as totalSessions, " +
           "SUM(CASE WHEN s.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedSessions, " +
           "SUM(CASE WHEN s.status = 'ACTIVE' THEN 1 ELSE 0 END) as activeSessions, " +
           "SUM(CASE WHEN s.status = 'PAUSED' THEN 1 ELSE 0 END) as pausedSessions, " +
           "AVG(s.completedCount) as avgCompletedCount, " +
           "AVG(s.correctCount) as avgCorrectCount " +
           "FROM ReviewSession s WHERE s.userId = :userId AND s.isActive = true")
    Object[] getSessionStatisticsByUserId(@Param("userId") String userId);

    /**
     * 查询指定状态的会话数量
     */
    @Query("SELECT COUNT(s) FROM ReviewSession s WHERE s.userId = :userId AND s.status = :status AND s.isActive = true")
    Long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    /**
     * 查询用户最近的N个会话
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.userId = :userId AND s.isActive = true " +
           "ORDER BY s.createdAt DESC")
    List<ReviewSession> findRecentSessionsByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * 查询超时的活跃会话
     * 超过指定时间仍处于ACTIVE状态的会话
     */
    @Query("SELECT s FROM ReviewSession s WHERE s.status = 'ACTIVE' " +
           "AND s.startTime < :timeoutThreshold AND s.isActive = true")
    List<ReviewSession> findTimeoutActiveSessions(@Param("timeoutThreshold") Long timeoutThreshold);

    /**
     * 查询需要自动保存的会话
     * 长时间暂停或活跃的会话
     */
    @Query("SELECT s FROM ReviewSession s WHERE " +
           "(s.status = 'PAUSED' AND s.pauseTime < :pauseThreshold) OR " +
           "(s.status = 'ACTIVE' AND s.startTime < :activeThreshold) " +
           "AND s.isActive = true")
    List<ReviewSession> findSessionsNeedingAutoSave(@Param("pauseThreshold") Long pauseThreshold,
                                                   @Param("activeThreshold") Long activeThreshold);

    /**
     * 根据复习模式统计会话数量
     */
    @Query("SELECT s.reviewMode, COUNT(s) FROM ReviewSession s " +
           "WHERE s.userId = :userId AND s.isActive = true " +
           "GROUP BY s.reviewMode ORDER BY COUNT(s) DESC")
    List<Object[]> countByReviewModeForUser(@Param("userId") String userId);

    /**
     * 查询用户的平均会话完成率
     * 简化查询，避免CAST函数
     */
    @Query("SELECT AVG(s.completedCount * 1.0 / s.totalQuestions) " +
           "FROM ReviewSession s WHERE s.userId = :userId " +
           "AND s.totalQuestions > 0 AND s.isActive = true")
    Double getAverageCompletionRateByUserId(@Param("userId") String userId);

    /**
     * 查询用户的平均正确率
     * 简化查询，避免CAST函数
     */
    @Query("SELECT AVG(s.correctCount * 1.0 / s.completedCount) " +
           "FROM ReviewSession s WHERE s.userId = :userId " +
           "AND s.completedCount > 0 AND s.isActive = true")
    Double getAverageAccuracyRateByUserId(@Param("userId") String userId);

    /**
     * 软删除会话
     */
    @Query("UPDATE ReviewSession s SET s.isActive = false, s.updatedAt = :currentTime " +
           "WHERE s.id = :sessionId AND s.userId = :userId")
    void softDeleteByIdAndUserId(@Param("sessionId") String sessionId, 
                                @Param("userId") String userId, 
                                @Param("currentTime") Long currentTime);
} 