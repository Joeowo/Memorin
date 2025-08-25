package com.memorin.knowledge.repository;

import com.memorin.knowledge.entity.BaseKnowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 基础知识点数据访问接口
 * 提供知识点的增删改查和复杂查询功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Repository
public interface BaseKnowledgeRepository extends JpaRepository<BaseKnowledge, String> {

    /**
     * 根据用户ID查询所有激活的知识点
     * 按创建时间倒序排列
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true " +
           "ORDER BY k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndIsActiveTrue(@Param("userId") String userId);

    /**
     * 根据用户ID和分类ID查询知识点
     * 按难度和创建时间排序
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.categoryId = :categoryId AND k.isActive = true " +
           "ORDER BY k.difficulty ASC, k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId);

    /**
     * 根据用户ID、分类ID和状态查询知识点
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.categoryId = :categoryId AND k.status = :status AND k.isActive = true " +
           "ORDER BY k.difficulty ASC, k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndCategoryIdAndStatus(@Param("userId") String userId, 
                                                          @Param("categoryId") String categoryId, 
                                                          @Param("status") String status);

    /**
     * 根据用户ID和题目类型查询知识点
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.type = :type AND k.isActive = true " +
           "ORDER BY k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndType(@Param("userId") String userId, @Param("type") String type);

    /**
     * 根据用户ID和难度等级查询知识点
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.difficulty = :difficulty AND k.isActive = true " +
           "ORDER BY k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndDifficulty(@Param("userId") String userId, @Param("difficulty") Integer difficulty);

    /**
     * 根据用户ID和难度范围查询知识点
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.difficulty BETWEEN :minDifficulty AND :maxDifficulty AND k.isActive = true " +
           "ORDER BY k.difficulty ASC, k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndDifficultyRange(@Param("userId") String userId, 
                                                      @Param("minDifficulty") Integer minDifficulty, 
                                                      @Param("maxDifficulty") Integer maxDifficulty);

    /**
     * 根据用户ID和状态查询知识点
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.status = :status AND k.isActive = true " +
           "ORDER BY k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    /**
     * 根据用户ID、知识点ID和激活状态查询知识点
     */
    Optional<BaseKnowledge> findByIdAndUserIdAndIsActive(String id, String userId, Boolean isActive);

    /**
     * 根据用户ID搜索知识点（问题内容和解析）
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true " +
           "AND (LOWER(k.question) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(k.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(k.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY k.createdAt DESC")
    List<BaseKnowledge> searchByUserIdAndKeyword(@Param("userId") String userId, @Param("keyword") String keyword);

    /**
     * 根据用户ID和标签搜索知识点
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true " +
           "AND LOWER(k.tags) LIKE LOWER(CONCAT('%', :tag, '%')) " +
           "ORDER BY k.createdAt DESC")
    List<BaseKnowledge> findByUserIdAndTag(@Param("userId") String userId, @Param("tag") String tag);

    /**
     * 统计用户的知识点总数
     */
    @Query("SELECT COUNT(k) FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true")
    long countByUserIdAndIsActive(@Param("userId") String userId);

    /**
     * 统计用户指定分类下的知识点数量
     */
    @Query("SELECT COUNT(k) FROM BaseKnowledge k WHERE k.userId = :userId AND k.categoryId = :categoryId AND k.isActive = true")
    long countByUserIdAndCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId);

    /**
     * 统计用户各状态下的知识点数量
     */
    @Query("SELECT k.status, COUNT(k) FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true GROUP BY k.status")
    List<Object[]> countByUserIdAndStatus(@Param("userId") String userId);

    /**
     * 统计用户各难度等级的知识点数量
     */
    @Query("SELECT k.difficulty, COUNT(k) FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true GROUP BY k.difficulty ORDER BY k.difficulty")
    List<Object[]> countByUserIdAndDifficulty(@Param("userId") String userId);

    /**
     * 统计用户各题型的知识点数量
     */
    @Query("SELECT k.type, COUNT(k) FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true GROUP BY k.type")
    List<Object[]> countByUserIdAndType(@Param("userId") String userId);

    /**
     * 根据用户ID分页查询知识点
     */
    Page<BaseKnowledge> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * 根据用户ID和分类ID分页查询知识点
     */
    Page<BaseKnowledge> findByUserIdAndCategoryIdAndIsActiveTrueOrderByDifficultyAscCreatedAtDesc(String userId, String categoryId, Pageable pageable);

    /**
     * 复合条件分页查询知识点
     */
    @Query("SELECT k FROM BaseKnowledge k WHERE k.userId = :userId AND k.isActive = true " +
           "AND (:categoryId IS NULL OR k.categoryId = :categoryId) " +
           "AND (:type IS NULL OR k.type = :type) " +
           "AND (:status IS NULL OR k.status = :status) " +
           "AND (:difficulty IS NULL OR k.difficulty = :difficulty) " +
           "ORDER BY k.createdAt DESC")
    Page<BaseKnowledge> findByComplexConditions(@Param("userId") String userId,
                                               @Param("categoryId") String categoryId,
                                               @Param("type") String type,
                                               @Param("status") String status,
                                               @Param("difficulty") Integer difficulty,
                                               Pageable pageable);

    /**
     * 检查分类下是否存在知识点（用于删除分类时验证）
     */
    @Query("SELECT COUNT(k) > 0 FROM BaseKnowledge k WHERE k.categoryId = :categoryId AND k.isActive = true")
    boolean existsKnowledgeInCategory(@Param("categoryId") String categoryId);

    /**
     * 根据分类ID批量更新知识点的分类（用于分类合并时）
     */
    @Query("UPDATE BaseKnowledge k SET k.categoryId = :newCategoryId, k.updatedAt = :updateTime " +
           "WHERE k.categoryId = :oldCategoryId AND k.isActive = true")
    int updateCategoryIdBatch(@Param("oldCategoryId") String oldCategoryId, 
                             @Param("newCategoryId") String newCategoryId, 
                             @Param("updateTime") Long updateTime);
} 