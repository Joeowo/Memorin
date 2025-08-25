package com.memorin.knowledge.service;

import com.memorin.knowledge.dto.request.CreateBaseKnowledgeRequest;
import com.memorin.knowledge.dto.request.UpdateBaseKnowledgeRequest;
import com.memorin.knowledge.dto.response.BaseKnowledgeResponse;
import com.memorin.knowledge.entity.BaseKnowledge;
import com.memorin.knowledge.repository.BaseKnowledgeRepository;
import com.memorin.knowledge.repository.CategoryRepository;
import com.memorin.knowledge.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础知识点业务逻辑服务
 * 包含知识点的CRUD操作、ID生成、分类验证等核心业务逻辑
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@Transactional
public class BaseKnowledgeService {

    private static final Logger logger = LoggerFactory.getLogger(BaseKnowledgeService.class);

    @Autowired
    private BaseKnowledgeRepository baseKnowledgeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 创建新知识点
     */
    public BaseKnowledgeResponse createKnowledge(CreateBaseKnowledgeRequest request, String userId) {
        logger.info("创建新知识点，用户ID: {}, 题型: {}, 分类ID: {}", userId, request.getType(), request.getCategoryId());

        // 1. 验证分类是否存在
        boolean categoryExists = categoryRepository.findByIdAndUserIdAndIsActive(
            request.getCategoryId(), userId, true).isPresent();
        if (!categoryExists) {
            throw new IllegalArgumentException("指定的分类不存在或已被删除");
        }

        // 2. 生成知识点ID
        String knowledgeId = generateKnowledgeId(request.getType());

        // 3. 创建知识点实体
        BaseKnowledge knowledge = new BaseKnowledge();
        knowledge.setId(knowledgeId);
        knowledge.setQuestion(request.getQuestion());
        knowledge.setExplanation(request.getExplanation());
        knowledge.setCategoryId(request.getCategoryId());
        knowledge.setType(request.getType());
        knowledge.setTagList(request.getTags()); // 使用实体的setTagList方法
        knowledge.setDifficulty(request.getDifficulty());
        knowledge.setEstimatedTime(request.getEstimatedTime());
        knowledge.setStatus(request.getStatus());
        knowledge.setIsActive(true);
        knowledge.setUserId(userId);

        // 4. 保存到数据库
        BaseKnowledge savedKnowledge = baseKnowledgeRepository.save(knowledge);
        logger.info("成功创建知识点，ID: {}, 题目: {}", savedKnowledge.getId(), 
                   savedKnowledge.getQuestion().substring(0, Math.min(savedKnowledge.getQuestion().length(), 30)));

        return BaseKnowledgeResponse.fromEntity(savedKnowledge);
    }

    /**
     * 更新知识点
     */
    public BaseKnowledgeResponse updateKnowledge(String knowledgeId, UpdateBaseKnowledgeRequest request, String userId) {
        logger.info("更新知识点，ID: {}, 用户ID: {}", knowledgeId, userId);

        // 1. 查找知识点
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(knowledgeId, userId, true)
            .orElseThrow(() -> new IllegalArgumentException("知识点不存在或已被删除"));

        // 2. 验证新分类是否存在
        boolean categoryExists = categoryRepository.findByIdAndUserIdAndIsActive(
            request.getCategoryId(), userId, true).isPresent();
        if (!categoryExists) {
            throw new IllegalArgumentException("指定的分类不存在或已被删除");
        }

        // 3. 更新知识点信息
        knowledge.setQuestion(request.getQuestion());
        knowledge.setExplanation(request.getExplanation());
        knowledge.setCategoryId(request.getCategoryId());
        knowledge.setTagList(request.getTags());
        knowledge.setDifficulty(request.getDifficulty());
        knowledge.setEstimatedTime(request.getEstimatedTime());
        knowledge.setStatus(request.getStatus());

        // 4. 保存更新
        BaseKnowledge updatedKnowledge = baseKnowledgeRepository.save(knowledge);
        logger.info("成功更新知识点，ID: {}", updatedKnowledge.getId());

        return BaseKnowledgeResponse.fromEntity(updatedKnowledge);
    }

    /**
     * 删除知识点（软删除）
     */
    public void deleteKnowledge(String knowledgeId, String userId) {
        logger.info("删除知识点，ID: {}, 用户ID: {}", knowledgeId, userId);

        // 1. 查找知识点
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(knowledgeId, userId, true)
            .orElseThrow(() -> new IllegalArgumentException("知识点不存在或已被删除"));

        // 2. 执行软删除
        knowledge.setIsActive(false);
        baseKnowledgeRepository.save(knowledge);

        logger.info("成功删除知识点，ID: {}", knowledgeId);
    }

    /**
     * 获取单个知识点详情
     */
    @Transactional(readOnly = true)
    public BaseKnowledgeResponse getKnowledgeById(String knowledgeId, String userId) {
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(knowledgeId, userId, true)
            .orElseThrow(() -> new IllegalArgumentException("知识点不存在或已被删除"));

        return BaseKnowledgeResponse.fromEntity(knowledge);
    }

    /**
     * 获取用户的所有知识点
     */
    @Transactional(readOnly = true)
    public List<BaseKnowledgeResponse> getAllKnowledge(String userId) {
        List<BaseKnowledge> knowledgeList = baseKnowledgeRepository.findByUserIdAndIsActiveTrue(userId);
        return knowledgeList.stream()
            .map(BaseKnowledgeResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 根据分类ID获取知识点
     */
    @Transactional(readOnly = true)
    public List<BaseKnowledgeResponse> getKnowledgeByCategory(String userId, String categoryId) {
        List<BaseKnowledge> knowledgeList = baseKnowledgeRepository.findByUserIdAndCategoryId(userId, categoryId);
        return knowledgeList.stream()
            .map(BaseKnowledgeResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 根据题型获取知识点
     */
    @Transactional(readOnly = true)
    public List<BaseKnowledgeResponse> getKnowledgeByType(String userId, String type) {
        List<BaseKnowledge> knowledgeList = baseKnowledgeRepository.findByUserIdAndType(userId, type);
        return knowledgeList.stream()
            .map(BaseKnowledgeResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 根据难度获取知识点
     */
    @Transactional(readOnly = true)
    public List<BaseKnowledgeResponse> getKnowledgeByDifficulty(String userId, Integer difficulty) {
        List<BaseKnowledge> knowledgeList = baseKnowledgeRepository.findByUserIdAndDifficulty(userId, difficulty);
        return knowledgeList.stream()
            .map(BaseKnowledgeResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 根据状态获取知识点
     */
    @Transactional(readOnly = true)
    public List<BaseKnowledgeResponse> getKnowledgeByStatus(String userId, String status) {
        List<BaseKnowledge> knowledgeList = baseKnowledgeRepository.findByUserIdAndStatus(userId, status);
        return knowledgeList.stream()
            .map(BaseKnowledgeResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 分页获取知识点列表
     */
    @Transactional(readOnly = true)
    public Page<BaseKnowledgeResponse> getKnowledgeWithPagination(String userId, Pageable pageable) {
        Page<BaseKnowledge> knowledgePage = baseKnowledgeRepository
            .findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId, pageable);
        
        return knowledgePage.map(BaseKnowledgeResponse::fromEntity);
    }

    /**
     * 复合条件分页查询知识点
     */
    @Transactional(readOnly = true)
    public Page<BaseKnowledgeResponse> getKnowledgeByComplexConditions(String userId, String categoryId, 
                                                                      String type, String status, 
                                                                      Integer difficulty, Pageable pageable) {
        Page<BaseKnowledge> knowledgePage = baseKnowledgeRepository.findByComplexConditions(
            userId, categoryId, type, status, difficulty, pageable);
        
        return knowledgePage.map(BaseKnowledgeResponse::fromEntity);
    }

    /**
     * 搜索知识点
     */
    @Transactional(readOnly = true)
    public List<BaseKnowledgeResponse> searchKnowledge(String userId, String keyword) {
        List<BaseKnowledge> knowledgeList = baseKnowledgeRepository.searchByUserIdAndKeyword(userId, keyword);
        return knowledgeList.stream()
            .map(BaseKnowledgeResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 根据标签搜索知识点
     */
    @Transactional(readOnly = true)
    public List<BaseKnowledgeResponse> getKnowledgeByTag(String userId, String tag) {
        List<BaseKnowledge> knowledgeList = baseKnowledgeRepository.findByUserIdAndTag(userId, tag);
        return knowledgeList.stream()
            .map(BaseKnowledgeResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 获取知识点统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getKnowledgeStatistics(String userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总数统计
        long totalCount = baseKnowledgeRepository.countByUserIdAndIsActive(userId);
        statistics.put("totalCount", totalCount);
        
        // 状态统计
        List<Object[]> statusStats = baseKnowledgeRepository.countByUserIdAndStatus(userId);
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusMap.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("statusStats", statusMap);
        
        // 难度统计
        List<Object[]> difficultyStats = baseKnowledgeRepository.countByUserIdAndDifficulty(userId);
        Map<Integer, Long> difficultyMap = new HashMap<>();
        for (Object[] stat : difficultyStats) {
            difficultyMap.put((Integer) stat[0], (Long) stat[1]);
        }
        statistics.put("difficultyStats", difficultyMap);
        
        // 题型统计
        List<Object[]> typeStats = baseKnowledgeRepository.countByUserIdAndType(userId);
        Map<String, Long> typeMap = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeMap.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("typeStats", typeMap);
        
        return statistics;
    }

    /**
     * 生成知识点ID
     * 格式: KP_{type}_{date}_{seq}
     */
    private String generateKnowledgeId(String type) {
        // 生成日期字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        
        // 获取当日已有的知识点数量作为序号
        long todayCount = getTodayKnowledgeCount() + 1;
        String seq = String.format("%03d", todayCount);
        
        // 组合ID
        return String.format("KP_%s_%s_%s", type.toUpperCase(), dateStr, seq);
    }

    /**
     * 获取今日已创建的知识点数量
     */
    private long getTodayKnowledgeCount() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todayStr = dateFormat.format(new Date());
        
        // 查询ID包含今日日期的知识点数量
        List<BaseKnowledge> todayKnowledge = baseKnowledgeRepository.findAll().stream()
            .filter(k -> k.getId().contains(todayStr))
            .collect(Collectors.toList());
            
        return todayKnowledge.size();
    }
} 