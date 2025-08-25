package com.memorin.knowledge.repository;

import com.memorin.knowledge.entity.ChoiceQuestionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 选择题数据Repository
 * 提供完整的数据访问功能
 */
@Repository
public interface ChoiceQuestionDataRepository extends JpaRepository<ChoiceQuestionData, String> {
    
    // =========================== 基础查询方法 ===========================
    
    /**
     * 根据知识点ID查询选择题数据
     */
    Optional<ChoiceQuestionData> findByKnowledgeId(String knowledgeId);
    
    /**
     * 根据知识点ID和用户ID查询选择题数据（权限控制）
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE cqd.knowledgeId = :knowledgeId " +
           "AND EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId)")
    Optional<ChoiceQuestionData> findByKnowledgeIdAndUserId(@Param("knowledgeId") String knowledgeId, 
                                                           @Param("userId") String userId);
    
    /**
     * 根据ID和用户ID查询（权限控制）
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE cqd.id = :id " +
           "AND EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId)")
    Optional<ChoiceQuestionData> findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
    
    // =========================== 按类型查询 ===========================
    
    /**
     * 根据选择题类型查询
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE cqd.choiceType = :choiceType " +
           "AND EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId) " +
           "ORDER BY cqd.createdAt DESC")
    List<ChoiceQuestionData> findByChoiceTypeAndUserId(@Param("choiceType") String choiceType, 
                                                       @Param("userId") String userId);
    
    /**
     * 根据分值范围查询
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE cqd.points BETWEEN :minPoints AND :maxPoints " +
           "AND EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId) " +
           "ORDER BY cqd.points ASC")
    List<ChoiceQuestionData> findByPointsRangeAndUserId(@Param("minPoints") Integer minPoints, 
                                                        @Param("maxPoints") Integer maxPoints, 
                                                        @Param("userId") String userId);
    
    // =========================== 按用户查询 ===========================
    
    /**
     * 查询用户的所有选择题数据
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId) " +
           "ORDER BY cqd.createdAt DESC")
    List<ChoiceQuestionData> findAllByUserId(@Param("userId") String userId);
    
    /**
     * 根据分类ID查询选择题数据
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk " +
           "WHERE bk.id = cqd.knowledgeId AND bk.categoryId = :categoryId AND bk.userId = :userId) " +
           "ORDER BY cqd.createdAt DESC")
    List<ChoiceQuestionData> findByCategoryIdAndUserId(@Param("categoryId") String categoryId, 
                                                       @Param("userId") String userId);
    
    // =========================== 分页查询 ===========================
    
    /**
     * 分页查询用户的选择题数据
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId)")
    Page<ChoiceQuestionData> findByUserIdWithPagination(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 根据复合条件分页查询
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk " +
           "WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId " +
           "AND (:categoryId IS NULL OR bk.categoryId = :categoryId)) " +
           "AND (:choiceType IS NULL OR cqd.choiceType = :choiceType) " +
           "AND (:minPoints IS NULL OR cqd.points >= :minPoints) " +
           "AND (:maxPoints IS NULL OR cqd.points <= :maxPoints)")
    Page<ChoiceQuestionData> findByComplexConditions(@Param("userId") String userId,
                                                     @Param("categoryId") String categoryId,
                                                     @Param("choiceType") String choiceType,
                                                     @Param("minPoints") Integer minPoints,
                                                     @Param("maxPoints") Integer maxPoints,
                                                     Pageable pageable);
    
    // =========================== 搜索查询 ===========================
    
    /**
     * 根据关键词搜索选择题数据（搜索知识点问题和选择题解析）
     */
    @Query("SELECT DISTINCT cqd FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk " +
           "WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId " +
           "AND (LOWER(bk.question) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(bk.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(bk.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "OR LOWER(cqd.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY cqd.createdAt DESC")
    List<ChoiceQuestionData> searchByKeyword(@Param("userId") String userId, @Param("keyword") String keyword);
    
    /**
     * 搜索包含特定选项内容的选择题
     */
    @Query("SELECT DISTINCT cqd FROM ChoiceQuestionData cqd " +
           "JOIN cqd.options opt " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId) " +
           "AND LOWER(opt.optionText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY cqd.createdAt DESC")
    List<ChoiceQuestionData> searchByOptionContent(@Param("userId") String userId, @Param("keyword") String keyword);
    
    // =========================== 统计查询 ===========================
    
    /**
     * 统计用户的选择题总数
     */
    @Query("SELECT COUNT(cqd) FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId)")
    long countByUserId(@Param("userId") String userId);
    
    /**
     * 按选择题类型统计
     */
    @Query("SELECT cqd.choiceType, COUNT(cqd) FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId) " +
           "GROUP BY cqd.choiceType")
    List<Object[]> countByChoiceTypeAndUserId(@Param("userId") String userId);
    
    /**
     * 按分值统计
     */
    @Query("SELECT " +
           "SUM(CASE WHEN cqd.points <= 20 THEN 1 ELSE 0 END) as low, " +
           "SUM(CASE WHEN cqd.points > 20 AND cqd.points <= 50 THEN 1 ELSE 0 END) as medium, " +
           "SUM(CASE WHEN cqd.points > 50 THEN 1 ELSE 0 END) as high " +
           "FROM ChoiceQuestionData cqd " +
           "WHERE EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId)")
    Object[] countByPointsRangeAndUserId(@Param("userId") String userId);
    
    /**
     * 按分类统计
     */
    @Query("SELECT bk.categoryId, COUNT(cqd) FROM ChoiceQuestionData cqd " +
           "JOIN BaseKnowledge bk ON bk.id = cqd.knowledgeId " +
           "WHERE bk.userId = :userId " +
           "GROUP BY bk.categoryId")
    List<Object[]> countByCategoryAndUserId(@Param("userId") String userId);
    
    /**
     * 统计支持部分得分的多选题数量
     */
    @Query("SELECT COUNT(cqd) FROM ChoiceQuestionData cqd " +
           "WHERE cqd.choiceType = 'multiple' AND cqd.partialCredit = true " +
           "AND EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId)")
    long countMultipleChoiceWithPartialCreditByUserId(@Param("userId") String userId);
    
    /**
     * 统计设置随机排序的选择题数量
     */
    @Query("SELECT COUNT(cqd) FROM ChoiceQuestionData cqd " +
           "WHERE cqd.randomOrder = true " +
           "AND EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId)")
    long countRandomOrderByUserId(@Param("userId") String userId);
    
    // =========================== 时间范围查询 ===========================
    
    /**
     * 查询指定时间范围内创建的选择题数据
     */
    @Query("SELECT cqd FROM ChoiceQuestionData cqd " +
           "WHERE cqd.createdAt BETWEEN :startTime AND :endTime " +
           "AND EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId) " +
           "ORDER BY cqd.createdAt DESC")
    List<ChoiceQuestionData> findByTimeRangeAndUserId(@Param("startTime") Long startTime, 
                                                      @Param("endTime") Long endTime, 
                                                      @Param("userId") String userId);
    
    /**
     * 统计今日创建的选择题数量（用于ID生成）
     */
    @Query("SELECT COUNT(cqd) FROM ChoiceQuestionData cqd " +
           "WHERE cqd.createdAt >= :startOfDay AND cqd.createdAt < :endOfDay")
    long countTodayCreated(@Param("startOfDay") Long startOfDay, @Param("endOfDay") Long endOfDay);
} 