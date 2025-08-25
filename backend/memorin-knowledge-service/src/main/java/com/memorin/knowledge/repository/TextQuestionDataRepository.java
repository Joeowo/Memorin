package com.memorin.knowledge.repository;

import com.memorin.knowledge.entity.TextQuestionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 文本题数据访问接口
 * 提供文本题数据的增删改查和复杂查询功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Repository
public interface TextQuestionDataRepository extends JpaRepository<TextQuestionData, String> {

    /**
     * 根据知识点ID查询文本题数据
     */
    Optional<TextQuestionData> findByKnowledgeId(String knowledgeId);

    /**
     * 根据知识点ID列表批量查询文本题数据
     */
    @Query("SELECT t FROM TextQuestionData t WHERE t.knowledgeId IN :knowledgeIds ORDER BY t.createdAt DESC")
    List<TextQuestionData> findByKnowledgeIdIn(@Param("knowledgeIds") List<String> knowledgeIds);

    /**
     * 根据文本题类型查询
     */
    @Query("SELECT t FROM TextQuestionData t WHERE t.textType = :textType ORDER BY t.createdAt DESC")
    List<TextQuestionData> findByTextType(@Param("textType") String textType);

    /**
     * 根据验证模式查询
     */
    @Query("SELECT t FROM TextQuestionData t WHERE t.validationMode = :validationMode ORDER BY t.createdAt DESC")
    List<TextQuestionData> findByValidationMode(@Param("validationMode") String validationMode);

    /**
     * 查询所有填空题
     */
    @Query("SELECT t FROM TextQuestionData t WHERE t.textType = 'fill' ORDER BY t.createdAt DESC")
    List<TextQuestionData> findAllFillQuestions();

    /**
     * 查询所有问答题
     */
    @Query("SELECT t FROM TextQuestionData t WHERE t.textType = 'essay' ORDER BY t.createdAt DESC")
    List<TextQuestionData> findAllEssayQuestions();

    /**
     * 根据文本题类型分页查询
     */
    Page<TextQuestionData> findByTextTypeOrderByCreatedAtDesc(String textType, Pageable pageable);

    /**
     * 根据验证模式分页查询
     */
    Page<TextQuestionData> findByValidationModeOrderByCreatedAtDesc(String validationMode, Pageable pageable);

    /**
     * 关联BaseKnowledge查询 - 根据用户ID查询该用户的所有文本题
     */
    @Query("SELECT t FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true ORDER BY t.createdAt DESC")
    List<TextQuestionData> findByUserId(@Param("userId") String userId);

    /**
     * 关联BaseKnowledge查询 - 根据用户ID和文本题类型查询
     */
    @Query("SELECT t FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true AND t.textType = :textType ORDER BY t.createdAt DESC")
    List<TextQuestionData> findByUserIdAndTextType(@Param("userId") String userId, @Param("textType") String textType);

    /**
     * 关联BaseKnowledge查询 - 根据用户ID和分类ID查询
     */
    @Query("SELECT t FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.categoryId = :categoryId AND k.isActive = true ORDER BY t.createdAt DESC")
    List<TextQuestionData> findByUserIdAndCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId);

    /**
     * 关联BaseKnowledge查询 - 根据用户ID、分类ID和文本题类型查询
     */
    @Query("SELECT t FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.categoryId = :categoryId AND k.isActive = true AND t.textType = :textType ORDER BY t.createdAt DESC")
    List<TextQuestionData> findByUserIdAndCategoryIdAndTextType(@Param("userId") String userId, 
                                                               @Param("categoryId") String categoryId, 
                                                               @Param("textType") String textType);

    /**
     * 关联BaseKnowledge分页查询 - 根据用户ID分页查询
     */
    @Query("SELECT t FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true ORDER BY t.createdAt DESC")
    Page<TextQuestionData> findByUserIdWithPagination(@Param("userId") String userId, Pageable pageable);

    /**
     * 搜索文本题 - 在答案中搜索关键词
     */
    @Query("SELECT t FROM TextQuestionData t WHERE LOWER(t.answer) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY t.createdAt DESC")
    List<TextQuestionData> searchByAnswerKeyword(@Param("keyword") String keyword);

    /**
     * 关联BaseKnowledge搜索 - 根据用户ID在问题和答案中搜索
     */
    @Query("SELECT t FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true " +
           "AND (LOWER(k.question) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.answer) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<TextQuestionData> searchByUserIdAndKeyword(@Param("userId") String userId, @Param("keyword") String keyword);

    /**
     * 复合条件查询 - 支持多条件筛选
     */
    @Query("SELECT t FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true " +
           "AND (:categoryId IS NULL OR k.categoryId = :categoryId) " +
           "AND (:textType IS NULL OR t.textType = :textType) " +
           "AND (:validationMode IS NULL OR t.validationMode = :validationMode) " +
           "ORDER BY t.createdAt DESC")
    Page<TextQuestionData> findByComplexConditions(@Param("userId") String userId,
                                                   @Param("categoryId") String categoryId,
                                                   @Param("textType") String textType,
                                                   @Param("validationMode") String validationMode,
                                                   Pageable pageable);

    /**
     * 统计查询 - 统计用户的文本题总数
     */
    @Query("SELECT COUNT(t) FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true")
    long countByUserId(@Param("userId") String userId);

    /**
     * 统计查询 - 统计用户各文本题类型的数量
     */
    @Query("SELECT t.textType, COUNT(t) FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true GROUP BY t.textType")
    List<Object[]> countByUserIdAndTextType(@Param("userId") String userId);

    /**
     * 统计查询 - 统计用户各验证模式的数量
     */
    @Query("SELECT t.validationMode, COUNT(t) FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.isActive = true GROUP BY t.validationMode")
    List<Object[]> countByUserIdAndValidationMode(@Param("userId") String userId);

    /**
     * 统计查询 - 统计指定分类下的文本题数量
     */
    @Query("SELECT COUNT(t) FROM TextQuestionData t JOIN BaseKnowledge k ON t.knowledgeId = k.id " +
           "WHERE k.userId = :userId AND k.categoryId = :categoryId AND k.isActive = true")
    long countByUserIdAndCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId);

    /**
     * 检查知识点是否存在对应的文本题数据
     */
    boolean existsByKnowledgeId(String knowledgeId);

    /**
     * 根据知识点ID删除文本题数据
     */
    void deleteByKnowledgeId(String knowledgeId);

    /**
     * 批量删除 - 根据知识点ID列表
     */
    @Query("DELETE FROM TextQuestionData t WHERE t.knowledgeId IN :knowledgeIds")
    int deleteByKnowledgeIdIn(@Param("knowledgeIds") List<String> knowledgeIds);

    /**
     * 获取有评分要点的问答题数量
     */
    @Query("SELECT COUNT(t) FROM TextQuestionData t WHERE t.textType = 'essay' AND SIZE(t.scoringPoints) > 0")
    long countEssayQuestionsWithScoringPoints();

    /**
     * 获取没有评分要点的问答题
     */
    @Query("SELECT t FROM TextQuestionData t WHERE t.textType = 'essay' AND SIZE(t.scoringPoints) = 0 ORDER BY t.createdAt DESC")
    List<TextQuestionData> findEssayQuestionsWithoutScoringPoints();

    /**
     * 获取使用正则验证的题目
     */
    @Query("SELECT t FROM TextQuestionData t WHERE t.validationMode = 'regex' AND t.regexPattern IS NOT NULL ORDER BY t.createdAt DESC")
    List<TextQuestionData> findRegexValidationQuestions();
} 