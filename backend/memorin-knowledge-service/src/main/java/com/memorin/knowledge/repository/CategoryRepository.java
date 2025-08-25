package com.memorin.knowledge.repository;

import com.memorin.knowledge.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 分类数据访问接口
 * 提供分类的增删改查和复杂查询功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    /**
     * 根据用户ID查询所有激活的分类
     * 按层级和排序权重排序
     */
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.isActive = true " +
           "ORDER BY c.level ASC, c.sortOrder ASC, c.createdAt ASC")
    List<Category> findByUserIdAndIsActiveTrue(@Param("userId") String userId);

    /**
     * 根据用户ID和父分类ID查询子分类
     * 按排序权重排序
     */
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.parentId = :parentId AND c.isActive = true " +
           "ORDER BY c.sortOrder ASC, c.createdAt ASC")
    List<Category> findChildrenByUserIdAndParentId(@Param("userId") String userId, @Param("parentId") String parentId);

    /**
     * 根据用户ID查询顶级分类（无父分类）
     * 按排序权重排序
     */
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.parentId IS NULL AND c.isActive = true " +
           "ORDER BY c.sortOrder ASC, c.createdAt ASC")
    List<Category> findRootCategoriesByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和层级查询分类
     */
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.level = :level AND c.isActive = true " +
           "ORDER BY c.sortOrder ASC, c.createdAt ASC")
    List<Category> findByUserIdAndLevel(@Param("userId") String userId, @Param("level") Integer level);

    /**
     * 根据用户ID、父分类ID和分类名称查询是否存在
     * 用于检查同级分类名称重复
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.userId = :userId AND c.parentId = :parentId " +
           "AND c.name = :name AND c.isActive = true AND c.id != :excludeId")
    boolean existsByUserIdAndParentIdAndNameAndNotId(@Param("userId") String userId, 
                                                    @Param("parentId") String parentId, 
                                                    @Param("name") String name, 
                                                    @Param("excludeId") String excludeId);

    /**
     * 根据用户ID、父分类ID和分类名称查询是否存在（创建时检查）
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.userId = :userId AND c.parentId = :parentId " +
           "AND c.name = :name AND c.isActive = true")
    boolean existsByUserIdAndParentIdAndName(@Param("userId") String userId, 
                                           @Param("parentId") String parentId, 
                                           @Param("name") String name);

    /**
     * 根据用户ID、分类ID和激活状态查询分类
     */
    Optional<Category> findByIdAndUserIdAndIsActive(String id, String userId, Boolean isActive);

    /**
     * 根据路径模式查询分类（用于查找某个分类的所有子孙分类）
     */
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.path LIKE :pathPattern AND c.isActive = true " +
           "ORDER BY c.level ASC, c.sortOrder ASC, c.createdAt ASC")
    List<Category> findByUserIdAndPathStartingWith(@Param("userId") String userId, @Param("pathPattern") String pathPattern);

    /**
     * 统计用户的分类总数
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.userId = :userId AND c.isActive = true")
    long countByUserIdAndIsActive(@Param("userId") String userId);

    /**
     * 统计指定父分类下的子分类数量
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.userId = :userId AND c.parentId = :parentId AND c.isActive = true")
    long countChildrenByUserIdAndParentId(@Param("userId") String userId, @Param("parentId") String parentId);

    /**
     * 获取指定父分类下的最大排序权重
     */
    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM Category c WHERE c.userId = :userId AND c.parentId = :parentId AND c.isActive = true")
    Integer getMaxSortOrderByUserIdAndParentId(@Param("userId") String userId, @Param("parentId") String parentId);

    /**
     * 根据用户ID分页查询分类
     */
    Page<Category> findByUserIdAndIsActiveTrueOrderByLevelAscSortOrderAscCreatedAtAsc(String userId, Pageable pageable);

    /**
     * 根据分类名称模糊搜索
     */
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.isActive = true " +
           "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.level ASC, c.sortOrder ASC, c.createdAt ASC")
    List<Category> searchByUserIdAndKeyword(@Param("userId") String userId, @Param("keyword") String keyword);
} 