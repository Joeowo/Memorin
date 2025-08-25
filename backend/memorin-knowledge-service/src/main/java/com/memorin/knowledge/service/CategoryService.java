package com.memorin.knowledge.service;

import com.memorin.knowledge.dto.request.CreateCategoryRequest;
import com.memorin.knowledge.dto.request.UpdateCategoryRequest;
import com.memorin.knowledge.dto.response.CategoryResponse;
import com.memorin.knowledge.entity.Category;
import com.memorin.knowledge.repository.CategoryRepository;
import com.memorin.knowledge.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类业务逻辑服务
 * 包含分类的CRUD操作、层级管理、路径维护等核心业务逻辑
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@Transactional
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 创建新分类
     */
    public CategoryResponse createCategory(CreateCategoryRequest request, String userId) {
        logger.info("创建新分类，用户ID: {}, 分类名称: {}", userId, request.getName());

        // 1. 验证父分类是否存在
        Category parentCategory = null;
        if (request.getParentId() != null && !request.getParentId().trim().isEmpty()) {
            parentCategory = categoryRepository.findByIdAndUserIdAndIsActive(
                request.getParentId(), userId, true)
                .orElseThrow(() -> new IllegalArgumentException("父分类不存在或已被删除"));
        }

        // 2. 检查同级分类名称是否重复
        boolean nameExists = categoryRepository.existsByUserIdAndParentIdAndName(
            userId, request.getParentId(), request.getName());
        if (nameExists) {
            throw new IllegalArgumentException("同级分类中已存在相同名称的分类");
        }

        // 3. 确定分类层级
        int level = parentCategory != null ? parentCategory.getLevel() + 1 : 1;
        if (level > 5) {
            throw new IllegalArgumentException("分类层级不能超过5级");
        }

        // 4. 生成分类ID
        String categoryId = generateCategoryId(level, userId);

        // 5. 生成分类路径
        String path = generateCategoryPath(parentCategory, request.getName());

        // 6. 设置排序权重
        Integer sortOrder = request.getSortOrder();
        if (sortOrder == null) {
            Integer maxSortOrder = categoryRepository.getMaxSortOrderByUserIdAndParentId(
                userId, request.getParentId());
            sortOrder = maxSortOrder + 1;
        }

        // 7. 创建分类实体
        Category category = new Category();
        category.setId(categoryId);
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setLevel(level);
        category.setPath(path);
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setSortOrder(sortOrder);
        category.setIsActive(true);
        category.setUserId(userId);

        // 8. 保存到数据库
        Category savedCategory = categoryRepository.save(category);
        logger.info("成功创建分类，ID: {}, 名称: {}", savedCategory.getId(), savedCategory.getName());

        return CategoryResponse.fromEntity(savedCategory);
    }

    /**
     * 更新分类
     */
    public CategoryResponse updateCategory(String categoryId, UpdateCategoryRequest request, String userId) {
        logger.info("更新分类，ID: {}, 用户ID: {}", categoryId, userId);

        // 1. 查找分类
        Category category = categoryRepository.findByIdAndUserIdAndIsActive(categoryId, userId, true)
            .orElseThrow(() -> new IllegalArgumentException("分类不存在或已被删除"));

        // 2. 检查同级分类名称是否重复（排除自己）
        boolean nameExists = categoryRepository.existsByUserIdAndParentIdAndNameAndNotId(
            userId, category.getParentId(), request.getName(), categoryId);
        if (nameExists) {
            throw new IllegalArgumentException("同级分类中已存在相同名称的分类");
        }

        // 3. 更新分类信息
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        // 4. 更新父分类ID
        if (request.getParentId() != null) {
            // 验证父分类是否存在
            if (!request.getParentId().trim().isEmpty()) {
                Category newParentCategory = categoryRepository.findByIdAndUserIdAndIsActive(
                    request.getParentId(), userId, true)
                    .orElseThrow(() -> new IllegalArgumentException("父分类不存在或已被删除"));
                
                // 检查是否会形成循环引用
                if (request.getParentId().equals(categoryId)) {
                    throw new IllegalArgumentException("分类不能作为自己的父分类");
                }
                
                // 检查层级限制
                int newLevel = newParentCategory.getLevel() + 1;
                if (newLevel > 5) {
                    throw new IllegalArgumentException("分类层级不能超过5级");
                }
                
                // 检查同级分类名称是否重复
                boolean nameExistsInNewParent = categoryRepository.existsByUserIdAndParentIdAndNameAndNotId(
                    userId, request.getParentId(), request.getName(), categoryId);
                if (nameExistsInNewParent) {
                    throw new IllegalArgumentException("同级分类中已存在相同名称的分类");
                }
                
                category.setParentId(request.getParentId());
                category.setLevel(newLevel);
                
                // 更新分类路径
                String newPath = generateCategoryPath(newParentCategory, request.getName());
                updateCategoryPath(category, newPath, userId);
            } else {
                // 设置为空（顶级分类）
                category.setParentId(null);
                category.setLevel(1);
                String newPath = generateCategoryPath(null, request.getName());
                updateCategoryPath(category, newPath, userId);
            }
        } else if (!category.getName().equals(request.getName())) {
            // 如果名称发生变化但parentId不变，需要更新路径
            Category parentCategory = null;
            if (category.getParentId() != null) {
                parentCategory = categoryRepository.findByIdAndUserIdAndIsActive(
                    category.getParentId(), userId, true).orElse(null);
            }
            String newPath = generateCategoryPath(parentCategory, request.getName());
            updateCategoryPath(category, newPath, userId);
        }

        // 5. 保存更新
        Category updatedCategory = categoryRepository.save(category);
        logger.info("成功更新分类，ID: {}, 新名称: {}", updatedCategory.getId(), updatedCategory.getName());

        return CategoryResponse.fromEntity(updatedCategory);
    }

    /**
     * 删除分类（软删除）
     */
    public void deleteCategory(String categoryId, String userId) {
        logger.info("删除分类，ID: {}, 用户ID: {}", categoryId, userId);

        // 1. 查找分类
        Category category = categoryRepository.findByIdAndUserIdAndIsActive(categoryId, userId, true)
            .orElseThrow(() -> new IllegalArgumentException("分类不存在或已被删除"));

        // 2. 检查是否有子分类
        long childrenCount = categoryRepository.countChildrenByUserIdAndParentId(userId, categoryId);
        if (childrenCount > 0) {
            throw new IllegalArgumentException("该分类下还有子分类，请先删除所有子分类");
        }

        // 3. 执行软删除
        category.setIsActive(false);
        categoryRepository.save(category);

        logger.info("成功删除分类，ID: {}", categoryId);
    }

    /**
     * 获取单个分类详情
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(String categoryId, String userId) {
        Category category = categoryRepository.findByIdAndUserIdAndIsActive(categoryId, userId, true)
            .orElseThrow(() -> new IllegalArgumentException("分类不存在或已被删除"));

        CategoryResponse response = CategoryResponse.fromEntity(category);

        // 设置子分类数量
        long childrenCount = categoryRepository.countChildrenByUserIdAndParentId(userId, categoryId);
        response.setChildrenCount((int) childrenCount);

        return response;
    }

    /**
     * 获取用户的所有分类（扁平列表）
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(String userId) {
        List<Category> categories = categoryRepository.findByUserIdAndIsActiveTrue(userId);
        return categories.stream()
            .map(CategoryResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 获取分类树结构
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree(String userId) {
        // 1. 获取所有分类
        List<Category> allCategories = categoryRepository.findByUserIdAndIsActiveTrue(userId);
        
        // 2. 转换为响应对象
        Map<String, CategoryResponse> categoryMap = allCategories.stream()
            .collect(Collectors.toMap(
                Category::getId,
                CategoryResponse::fromEntity
            ));

        // 3. 构建树结构
        List<CategoryResponse> rootCategories = new ArrayList<>();
        for (CategoryResponse category : categoryMap.values()) {
            if (category.getParentId() == null) {
                // 顶级分类
                rootCategories.add(category);
            } else {
                // 子分类，添加到父分类下
                CategoryResponse parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.addChild(category);
                }
            }
        }

        return rootCategories;
    }

    /**
     * 分页获取分类列表
     */
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getCategoriesWithPagination(String userId, Pageable pageable) {
        Page<Category> categoryPage = categoryRepository
            .findByUserIdAndIsActiveTrueOrderByLevelAscSortOrderAscCreatedAtAsc(userId, pageable);
        
        return categoryPage.map(CategoryResponse::fromEntity);
    }

    /**
     * 搜索分类
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String userId, String keyword) {
        List<Category> categories = categoryRepository.searchByUserIdAndKeyword(userId, keyword);
        return categories.stream()
            .map(CategoryResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 生成分类ID
     * 格式: CAT_{level}_{seq}_{hash}
     */
    private String generateCategoryId(int level, String userId) {
        // 获取当前层级的分类数量作为序号
        long count = categoryRepository.countByUserIdAndIsActive(userId) + 1;
        String seq = String.format("%03d", count);
        
        // 生成简短的hash
        String hash = Integer.toHexString((userId + System.currentTimeMillis()).hashCode()).substring(0, 4).toUpperCase();
        
        return String.format("CAT_%d_%s_%s", level, seq, hash);
    }

    /**
     * 生成分类路径
     */
    private String generateCategoryPath(Category parentCategory, String categoryName) {
        if (parentCategory == null) {
            return categoryName;
        }
        return parentCategory.getPath() + "/" + categoryName;
    }

    /**
     * 更新分类路径（当分类名称变更时）
     */
    private void updateCategoryPath(Category category, String newName, String userId) {
        String oldPath = category.getPath();
        String newPath;
        
        if (category.getParentId() == null) {
            newPath = newName;
        } else {
            String[] pathParts = oldPath.split("/");
            pathParts[pathParts.length - 1] = newName;
            newPath = String.join("/", pathParts);
        }
        
        category.setPath(newPath);
        
        // 更新所有子孙分类的路径
        List<Category> descendants = categoryRepository.findByUserIdAndPathStartingWith(
            userId, oldPath + "/");
        
        for (Category descendant : descendants) {
            String descendantNewPath = descendant.getPath().replace(oldPath, newPath);
            descendant.setPath(descendantNewPath);
        }
        
        if (!descendants.isEmpty()) {
            categoryRepository.saveAll(descendants);
        }
    }
} 