package com.memorin.knowledge.repository;

import com.memorin.knowledge.entity.CodeQuestionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 编程题数据Repository接口
 * 提供编程题数据的数据访问操作
 */
@Repository
public interface CodeQuestionDataRepository extends JpaRepository<CodeQuestionData, String> {
    
    // =========================== 基础查询操作 ===========================
    
    /**
     * 根据ID和用户ID查询编程题（权限控制）
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE cqd.id = :id AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    Optional<CodeQuestionData> findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
    
    /**
     * 根据知识点ID和用户ID查询编程题
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE cqd.knowledgeId = :knowledgeId AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    Optional<CodeQuestionData> findByKnowledgeIdAndUserId(@Param("knowledgeId") String knowledgeId, @Param("userId") String userId);
    
    /**
     * 获取用户的所有编程题
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    List<CodeQuestionData> findAllByUserId(@Param("userId") String userId);
    
    // =========================== 按编程语言查询 ===========================
    
    /**
     * 根据编程语言和用户ID查询
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE cqd.programmingLanguage = :language AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    List<CodeQuestionData> findByProgrammingLanguageAndUserId(@Param("language") String programmingLanguage, @Param("userId") String userId);
    
    /**
     * 按编程语言统计数量
     */
    @Query("SELECT cqd.programmingLanguage, COUNT(cqd) FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "GROUP BY cqd.programmingLanguage")
    List<Object[]> countByProgrammingLanguageAndUserId(@Param("userId") String userId);
    
    // =========================== 按难度等级查询 ===========================
    
    /**
     * 根据难度等级和用户ID查询
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE cqd.difficultyLevel = :level AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    List<CodeQuestionData> findByDifficultyLevelAndUserId(@Param("level") Integer difficultyLevel, @Param("userId") String userId);
    
    /**
     * 根据难度范围查询
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE cqd.difficultyLevel >= :minLevel AND cqd.difficultyLevel <= :maxLevel AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.difficultyLevel, cqd.createdAt DESC")
    List<CodeQuestionData> findByDifficultyRangeAndUserId(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel, @Param("userId") String userId);
    
    /**
     * 按难度等级统计数量
     */
    @Query("SELECT cqd.difficultyLevel, COUNT(cqd) FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "GROUP BY cqd.difficultyLevel ORDER BY cqd.difficultyLevel")
    List<Object[]> countByDifficultyLevelAndUserId(@Param("userId") String userId);
    
    // =========================== 按分值查询 ===========================
    
    /**
     * 根据分值范围查询
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE cqd.points >= :minPoints AND cqd.points <= :maxPoints AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.points DESC, cqd.createdAt DESC")
    List<CodeQuestionData> findByPointsRangeAndUserId(@Param("minPoints") Integer minPoints, @Param("maxPoints") Integer maxPoints, @Param("userId") String userId);
    
    /**
     * 按分值范围统计数量
     */
    @Query("SELECT " +
           "SUM(CASE WHEN cqd.points <= 20 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN cqd.points > 20 AND cqd.points <= 50 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN cqd.points > 50 THEN 1 ELSE 0 END) " +
           "FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    Object[] countByPointsRangeAndUserId(@Param("userId") String userId);
    
    // =========================== 按分类查询 ===========================
    
    /**
     * 根据分类ID查询编程题
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.categoryId = :categoryId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    List<CodeQuestionData> findByCategoryIdAndUserId(@Param("categoryId") String categoryId, @Param("userId") String userId);
    
    /**
     * 按分类统计数量
     */
    @Query("SELECT bk.categoryId, COUNT(cqd) FROM CodeQuestionData cqd " +
           "JOIN BaseKnowledge bk ON bk.id = cqd.knowledgeId " +
           "WHERE bk.userId = :userId AND bk.isActive = true " +
           "GROUP BY bk.categoryId")
    List<Object[]> countByCategoryAndUserId(@Param("userId") String userId);
    
    // =========================== 分页查询 ===========================
    
    /**
     * 用户编程题分页查询
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    Page<CodeQuestionData> findByUserIdWithPagination(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 复合条件分页查询
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd " +
           "JOIN BaseKnowledge bk ON bk.id = cqd.knowledgeId " +
           "WHERE bk.userId = :userId AND bk.isActive = true " +
           "AND (:categoryId IS NULL OR bk.categoryId = :categoryId) " +
           "AND (:programmingLanguage IS NULL OR cqd.programmingLanguage = :programmingLanguage) " +
           "AND (:difficultyLevel IS NULL OR cqd.difficultyLevel = :difficultyLevel) " +
           "AND (:minPoints IS NULL OR cqd.points >= :minPoints) " +
           "AND (:maxPoints IS NULL OR cqd.points <= :maxPoints) " +
           "ORDER BY cqd.createdAt DESC")
    Page<CodeQuestionData> findByComplexConditions(@Param("userId") String userId,
                                                   @Param("categoryId") String categoryId,
                                                   @Param("programmingLanguage") String programmingLanguage,
                                                   @Param("difficultyLevel") Integer difficultyLevel,
                                                   @Param("minPoints") Integer minPoints,
                                                   @Param("maxPoints") Integer maxPoints,
                                                   Pageable pageable);
    
    // =========================== 搜索功能 ===========================
    
    /**
     * 根据关键词搜索编程题（标题和描述）
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "(LOWER(cqd.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(cqd.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    List<CodeQuestionData> searchByKeyword(@Param("userId") String userId, @Param("keyword") String keyword);
    
    /**
     * 根据代码内容搜索（初始代码和标准答案）
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "(LOWER(cqd.initialCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(cqd.standardAnswer) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    List<CodeQuestionData> searchByCodeContent(@Param("userId") String userId, @Param("keyword") String keyword);
    
    /**
     * 根据提示内容搜索
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "LOWER(cqd.hints) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.createdAt DESC")
    List<CodeQuestionData> searchByHints(@Param("userId") String userId, @Param("keyword") String keyword);
    
    // =========================== 统计查询 ===========================
    
    /**
     * 统计用户编程题总数
     */
    @Query("SELECT COUNT(cqd) FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    long countByUserId(@Param("userId") String userId);
    
    /**
     * 统计今日创建的编程题数量
     */
    @Query("SELECT COUNT(cqd) FROM CodeQuestionData cqd WHERE " +
           "cqd.createdAt >= :startOfDay AND cqd.createdAt < :endOfDay")
    long countTodayCreated(@Param("startOfDay") Long startOfDay, @Param("endOfDay") Long endOfDay);
    
    /**
     * 统计有初始代码模板的编程题数量
     */
    @Query("SELECT COUNT(cqd) FROM CodeQuestionData cqd WHERE " +
           "cqd.initialCode IS NOT NULL AND LENGTH(TRIM(cqd.initialCode)) > 0 AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    long countWithInitialCodeByUserId(@Param("userId") String userId);
    
    /**
     * 统计有解题提示的编程题数量
     */
    @Query("SELECT COUNT(cqd) FROM CodeQuestionData cqd WHERE " +
           "cqd.hints IS NOT NULL AND LENGTH(TRIM(cqd.hints)) > 0 AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    long countWithHintsByUserId(@Param("userId") String userId);
    
    /**
     * 统计有标准答案的编程题数量
     */
    @Query("SELECT COUNT(cqd) FROM CodeQuestionData cqd WHERE " +
           "cqd.standardAnswer IS NOT NULL AND LENGTH(TRIM(cqd.standardAnswer)) > 0 AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    long countWithStandardAnswerByUserId(@Param("userId") String userId);
    
    /**
     * 获取平均难度等级
     */
    @Query("SELECT AVG(cqd.difficultyLevel) FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    Double getAverageDifficultyByUserId(@Param("userId") String userId);
    
    /**
     * 获取平均分值
     */
    @Query("SELECT AVG(cqd.points) FROM CodeQuestionData cqd WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    Double getAveragePointsByUserId(@Param("userId") String userId);
    
    // =========================== 特殊查询 ===========================
    
    /**
     * 查找测试用例数量最多的编程题
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "SIZE(cqd.testCases) = (SELECT MAX(SIZE(cqd2.testCases)) FROM CodeQuestionData cqd2 WHERE " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk2 WHERE bk2.id = cqd2.knowledgeId AND bk2.userId = :userId AND bk2.isActive = true)) AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true)")
    List<CodeQuestionData> findMaxTestCasesByUserId(@Param("userId") String userId);
    
    /**
     * 按编程语言和难度联合查询
     */
    @Query("SELECT cqd FROM CodeQuestionData cqd WHERE " +
           "cqd.programmingLanguage = :language AND cqd.difficultyLevel = :level AND " +
           "EXISTS (SELECT 1 FROM BaseKnowledge bk WHERE bk.id = cqd.knowledgeId AND bk.userId = :userId AND bk.isActive = true) " +
           "ORDER BY cqd.points DESC, cqd.createdAt DESC")
    List<CodeQuestionData> findByLanguageAndDifficultyAndUserId(@Param("language") String programmingLanguage, 
                                                                @Param("level") Integer difficultyLevel, 
                                                                @Param("userId") String userId);
} 