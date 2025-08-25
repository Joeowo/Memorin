package com.memorin.knowledge.service;

import com.memorin.knowledge.dto.request.ChoiceOptionRequest;
import com.memorin.knowledge.dto.request.CreateChoiceQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateChoiceQuestionDataRequest;
import com.memorin.knowledge.dto.response.ChoiceQuestionDataResponse;
import com.memorin.knowledge.entity.BaseKnowledge;
import com.memorin.knowledge.entity.ChoiceOption;
import com.memorin.knowledge.entity.ChoiceQuestionData;
import com.memorin.knowledge.repository.BaseKnowledgeRepository;
import com.memorin.knowledge.repository.ChoiceQuestionDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 选择题数据业务逻辑服务
 */
@Service
@Transactional
public class ChoiceQuestionDataService {
    
    @Autowired
    private ChoiceQuestionDataRepository choiceQuestionDataRepository;
    
    @Autowired
    private BaseKnowledgeRepository baseKnowledgeRepository;
    
    // =========================== CRUD 操作 ===========================
    
    /**
     * 创建选择题数据
     */
    public ChoiceQuestionDataResponse createChoiceQuestionData(CreateChoiceQuestionDataRequest request, String userId) {
        // 数据验证
        validateChoiceQuestionDataRequest(request);
        
        // 验证知识点存在性和权限
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(request.getKnowledgeId(), userId, true)
                .orElseThrow(() -> new RuntimeException("知识点不存在或无权限访问"));
        
        // 检查该知识点是否已有选择题数据
        Optional<ChoiceQuestionData> existing = choiceQuestionDataRepository.findByKnowledgeIdAndUserId(
                request.getKnowledgeId(), userId);
        if (existing.isPresent()) {
            throw new RuntimeException("该知识点已存在选择题数据");
        }
        
        // 创建选择题数据实体
        ChoiceQuestionData choiceData = new ChoiceQuestionData();
        choiceData.setId(generateChoiceQuestionDataId());
        choiceData.setKnowledgeId(request.getKnowledgeId());
        choiceData.setChoiceType(request.getChoiceType());
        choiceData.setPoints(request.getPoints());
        choiceData.setPartialCredit(request.getPartialCredit() != null ? request.getPartialCredit() : false);
        choiceData.setRandomOrder(request.getRandomOrder() != null ? request.getRandomOrder() : false);
        choiceData.setExplanation(request.getExplanation());
        
        // 转换选项列表
        List<ChoiceOption> options = convertChoiceOptionRequests(request.getOptions());
        choiceData.setOptions(options);
        
        // 保存到数据库
        ChoiceQuestionData savedData = choiceQuestionDataRepository.save(choiceData);
        
        return ChoiceQuestionDataResponse.fromEntity(savedData);
    }
    
    /**
     * 更新选择题数据
     */
    public ChoiceQuestionDataResponse updateChoiceQuestionData(String choiceDataId, 
                                                               UpdateChoiceQuestionDataRequest request, 
                                                               String userId) {
        // 数据验证
        validateChoiceQuestionDataUpdateRequest(request);
        
        // 查找并验证权限
        ChoiceQuestionData existingData = findChoiceQuestionDataByIdAndUserId(choiceDataId, userId);
        
        // 更新基础字段
        existingData.setChoiceType(request.getChoiceType());
        existingData.setPoints(request.getPoints());
        existingData.setPartialCredit(request.getPartialCredit() != null ? request.getPartialCredit() : false);
        existingData.setRandomOrder(request.getRandomOrder() != null ? request.getRandomOrder() : false);
        existingData.setExplanation(request.getExplanation());
        
        // 更新选项列表
        List<ChoiceOption> options = convertChoiceOptionRequests(request.getOptions());
        existingData.setOptions(options);
        
        // 保存更新
        ChoiceQuestionData savedData = choiceQuestionDataRepository.save(existingData);
        
        return ChoiceQuestionDataResponse.fromEntity(savedData);
    }
    
    /**
     * 删除选择题数据
     */
    public void deleteChoiceQuestionData(String choiceDataId, String userId) {
        ChoiceQuestionData choiceData = findChoiceQuestionDataByIdAndUserId(choiceDataId, userId);
        choiceQuestionDataRepository.delete(choiceData);
    }
    
    // =========================== 查询操作 ===========================
    
    /**
     * 根据ID获取选择题数据
     */
    @Transactional(readOnly = true)
    public ChoiceQuestionDataResponse getChoiceQuestionDataById(String choiceDataId, String userId) {
        ChoiceQuestionData choiceData = findChoiceQuestionDataByIdAndUserId(choiceDataId, userId);
        return ChoiceQuestionDataResponse.fromEntity(choiceData);
    }
    
    /**
     * 根据知识点ID获取选择题数据
     */
    @Transactional(readOnly = true)
    public ChoiceQuestionDataResponse getChoiceQuestionDataByKnowledgeId(String knowledgeId, String userId) {
        ChoiceQuestionData choiceData = choiceQuestionDataRepository.findByKnowledgeIdAndUserId(knowledgeId, userId)
                .orElseThrow(() -> new RuntimeException("选择题数据不存在"));
        return ChoiceQuestionDataResponse.fromEntity(choiceData);
    }
    
    /**
     * 获取用户所有选择题数据
     */
    @Transactional(readOnly = true)
    public List<ChoiceQuestionDataResponse> getAllChoiceQuestionDataByUser(String userId) {
        List<ChoiceQuestionData> choiceDataList = choiceQuestionDataRepository.findAllByUserId(userId);
        return choiceDataList.stream()
                .map(ChoiceQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据选择题类型获取数据
     */
    @Transactional(readOnly = true)
    public List<ChoiceQuestionDataResponse> getChoiceQuestionDataByType(String userId, String choiceType) {
        List<ChoiceQuestionData> choiceDataList = choiceQuestionDataRepository.findByChoiceTypeAndUserId(choiceType, userId);
        return choiceDataList.stream()
                .map(ChoiceQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据分类ID获取选择题数据
     */
    @Transactional(readOnly = true)
    public List<ChoiceQuestionDataResponse> getChoiceQuestionDataByCategory(String userId, String categoryId) {
        List<ChoiceQuestionData> choiceDataList = choiceQuestionDataRepository.findByCategoryIdAndUserId(categoryId, userId);
        return choiceDataList.stream()
                .map(ChoiceQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取选择题数据
     */
    @Transactional(readOnly = true)
    public Page<ChoiceQuestionDataResponse> getChoiceQuestionDataWithPagination(String userId, Pageable pageable) {
        Page<ChoiceQuestionData> choiceDataPage = choiceQuestionDataRepository.findByUserIdWithPagination(userId, pageable);
        return choiceDataPage.map(ChoiceQuestionDataResponse::fromEntity);
    }
    
    /**
     * 根据复合条件分页查询
     */
    @Transactional(readOnly = true)
    public Page<ChoiceQuestionDataResponse> getChoiceQuestionDataByComplexConditions(String userId, 
                                                                                     String categoryId, 
                                                                                     String choiceType, 
                                                                                     Integer minPoints, 
                                                                                     Integer maxPoints, 
                                                                                     Pageable pageable) {
        Page<ChoiceQuestionData> choiceDataPage = choiceQuestionDataRepository.findByComplexConditions(
                userId, categoryId, choiceType, minPoints, maxPoints, pageable);
        return choiceDataPage.map(ChoiceQuestionDataResponse::fromEntity);
    }
    
    /**
     * 根据关键词搜索选择题数据
     */
    @Transactional(readOnly = true)
    public List<ChoiceQuestionDataResponse> searchChoiceQuestionData(String userId, String keyword) {
        List<ChoiceQuestionData> choiceDataList = choiceQuestionDataRepository.searchByKeyword(userId, keyword);
        return choiceDataList.stream()
                .map(ChoiceQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据选项内容搜索选择题
     */
    @Transactional(readOnly = true)
    public List<ChoiceQuestionDataResponse> searchChoiceQuestionDataByOptionContent(String userId, String keyword) {
        List<ChoiceQuestionData> choiceDataList = choiceQuestionDataRepository.searchByOptionContent(userId, keyword);
        return choiceDataList.stream()
                .map(ChoiceQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    // =========================== 统计分析 ===========================
    
    /**
     * 获取选择题数据统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getChoiceQuestionDataStatistics(String userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总数统计
        long totalCount = choiceQuestionDataRepository.countByUserId(userId);
        statistics.put("totalCount", totalCount);
        
        // 按类型统计
        List<Object[]> typeStats = choiceQuestionDataRepository.countByChoiceTypeAndUserId(userId);
        Map<String, Long> choiceTypeStats = new HashMap<>();
        for (Object[] stat : typeStats) {
            choiceTypeStats.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("choiceTypeStats", choiceTypeStats);
        
        // 按分值范围统计
        Object[] pointsStats = choiceQuestionDataRepository.countByPointsRangeAndUserId(userId);
        if (pointsStats != null && pointsStats.length >= 3) {
            Map<String, Long> pointsRangeStats = new HashMap<>();
            pointsRangeStats.put("low", (Long) pointsStats[0]);      // ≤20分
            pointsRangeStats.put("medium", (Long) pointsStats[1]);   // 21-50分
            pointsRangeStats.put("high", (Long) pointsStats[2]);     // >50分
            statistics.put("pointsRangeStats", pointsRangeStats);
        }
        
        // 按分类统计
        List<Object[]> categoryStats = choiceQuestionDataRepository.countByCategoryAndUserId(userId);
        Map<String, Long> categoryStatsMap = new HashMap<>();
        for (Object[] stat : categoryStats) {
            categoryStatsMap.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("categoryStats", categoryStatsMap);
        
        // 特殊配置统计
        long multipleChoiceWithPartialCredit = choiceQuestionDataRepository.countMultipleChoiceWithPartialCreditByUserId(userId);
        statistics.put("multipleChoiceWithPartialCreditCount", multipleChoiceWithPartialCredit);
        
        long randomOrderCount = choiceQuestionDataRepository.countRandomOrderByUserId(userId);
        statistics.put("randomOrderCount", randomOrderCount);
        
        return statistics;
    }
    
    // =========================== 私有辅助方法 ===========================
    
    /**
     * 验证创建选择题数据请求
     */
    private void validateChoiceQuestionDataRequest(CreateChoiceQuestionDataRequest request) {
        if (!request.isValidOptionConfiguration()) {
            throw new RuntimeException("选项配置不合理：单选题必须有且仅有一个正确答案，多选题至少要有一个正确答案，且选项键不能重复");
        }
        
        if (!request.isValidPartialCreditConfiguration()) {
            throw new RuntimeException("部分得分配置不合理：只有多选题才能设置部分得分");
        }
    }
    
    /**
     * 验证更新选择题数据请求
     */
    private void validateChoiceQuestionDataUpdateRequest(UpdateChoiceQuestionDataRequest request) {
        if (!request.isValidOptionConfiguration()) {
            throw new RuntimeException("选项配置不合理：单选题必须有且仅有一个正确答案，多选题至少要有一个正确答案，且选项键不能重复");
        }
        
        if (!request.isValidPartialCreditConfiguration()) {
            throw new RuntimeException("部分得分配置不合理：只有多选题才能设置部分得分");
        }
        
        if (!request.isValidSortOrderConfiguration()) {
            throw new RuntimeException("选项排序配置不合理：排序顺序必须从0开始且连续");
        }
    }
    
    /**
     * 转换选项请求为选项实体
     */
    private List<ChoiceOption> convertChoiceOptionRequests(List<ChoiceOptionRequest> requests) {
        if (requests == null) {
            return new ArrayList<>();
        }
        
        return requests.stream()
                .map(request -> new ChoiceOption(
                        request.getOptionKey(),
                        request.getOptionText(),
                        request.getIsCorrect(),
                        request.getExplanation(),
                        request.getSortOrder()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID和用户ID查找选择题数据（权限控制）
     */
    private ChoiceQuestionData findChoiceQuestionDataByIdAndUserId(String choiceDataId, String userId) {
        return choiceQuestionDataRepository.findByIdAndUserId(choiceDataId, userId)
                .orElseThrow(() -> new RuntimeException("选择题数据不存在或无权限访问"));
    }
    
    /**
     * 生成选择题数据ID
     * 格式：CQD_{date}_{seq}
     */
    private String generateChoiceQuestionDataId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = dateFormat.format(new Date());
        
        // 获取今日已创建的选择题数量
        long todayCount = getTodayChoiceQuestionDataCount();
        
        // 生成序列号（从001开始）
        String sequence = String.format("%03d", todayCount + 1);
        
        return "CQD_" + today + "_" + sequence;
    }
    
    /**
     * 获取今日创建的选择题数量
     */
    private long getTodayChoiceQuestionDataCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = calendar.getTimeInMillis();
        
        return choiceQuestionDataRepository.countTodayCreated(startOfDay, endOfDay);
    }
} 