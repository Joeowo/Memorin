package com.memorin.knowledge.service;

import com.memorin.knowledge.dto.request.CreateCodeQuestionDataRequest;
import com.memorin.knowledge.dto.request.TestCaseRequest;
import com.memorin.knowledge.dto.request.UpdateCodeQuestionDataRequest;
import com.memorin.knowledge.dto.response.CodeQuestionDataResponse;
import com.memorin.knowledge.entity.BaseKnowledge;
import com.memorin.knowledge.entity.CodeQuestionData;
import com.memorin.knowledge.entity.TestCase;
import com.memorin.knowledge.repository.BaseKnowledgeRepository;
import com.memorin.knowledge.repository.CodeQuestionDataRepository;
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
 * 编程题数据业务逻辑服务
 */
@Service
@Transactional
public class CodeQuestionDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(CodeQuestionDataService.class);
    
    @Autowired
    private CodeQuestionDataRepository codeQuestionDataRepository;
    
    @Autowired
    private BaseKnowledgeRepository baseKnowledgeRepository;
    
    // =========================== CRUD 操作 ===========================
    
    /**
     * 创建编程题数据
     */
    public CodeQuestionDataResponse createCodeQuestionData(CreateCodeQuestionDataRequest request, String userId) {
        logger.info("创建编程题数据，用户ID: {}, 知识点ID: {}, 编程语言: {}", userId, request.getKnowledgeId(), request.getProgrammingLanguage());
        
        // 数据验证
        validateCodeQuestionDataRequest(request);
        
        // 验证知识点存在性和权限
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(request.getKnowledgeId(), userId, true)
                .orElseThrow(() -> new RuntimeException("知识点不存在或无权限访问"));
        
        // 验证知识点类型是否为code
        if (!"code".equals(knowledge.getType())) {
            throw new RuntimeException("该知识点类型不是编程题，无法创建编程题数据");
        }
        
        // 检查该知识点是否已有编程题数据
        Optional<CodeQuestionData> existing = codeQuestionDataRepository.findByKnowledgeIdAndUserId(
                request.getKnowledgeId(), userId);
        if (existing.isPresent()) {
            throw new RuntimeException("该知识点已存在编程题数据");
        }
        
        // 创建编程题数据实体
        CodeQuestionData codeData = new CodeQuestionData();
        codeData.setId(generateCodeQuestionDataId());
        codeData.setKnowledgeId(request.getKnowledgeId());
        codeData.setProgrammingLanguage(request.getProgrammingLanguage());
        codeData.setTitle(request.getTitle());
        codeData.setDescription(request.getDescription());
        codeData.setDifficultyLevel(request.getDifficultyLevel());
        codeData.setPoints(request.getPoints());
        codeData.setInitialCode(request.getInitialCode());
        codeData.setHints(request.getHints());
        codeData.setStandardAnswer(request.getStandardAnswer());
        
        // 转换测试用例列表
        List<TestCase> testCases = convertTestCaseRequests(request.getTestCases());
        codeData.setTestCases(testCases);
        
        // 保存到数据库
        CodeQuestionData savedData = codeQuestionDataRepository.save(codeData);
        logger.info("编程题数据创建成功，ID: {}", savedData.getId());
        
        return CodeQuestionDataResponse.fromEntity(savedData);
    }
    
    /**
     * 更新编程题数据
     */
    public CodeQuestionDataResponse updateCodeQuestionData(String codeDataId, 
                                                          UpdateCodeQuestionDataRequest request, 
                                                          String userId) {
        logger.info("更新编程题数据，ID: {}, 用户ID: {}", codeDataId, userId);
        
        // 数据验证
        validateCodeQuestionDataUpdateRequest(request);
        
        // 查找并验证权限
        CodeQuestionData existingData = findCodeQuestionDataByIdAndUserId(codeDataId, userId);
        
        // 更新基础字段
        existingData.setProgrammingLanguage(request.getProgrammingLanguage());
        existingData.setTitle(request.getTitle());
        existingData.setDescription(request.getDescription());
        existingData.setDifficultyLevel(request.getDifficultyLevel());
        existingData.setPoints(request.getPoints());
        existingData.setInitialCode(request.getInitialCode());
        existingData.setHints(request.getHints());
        existingData.setStandardAnswer(request.getStandardAnswer());
        
        // 更新测试用例列表
        List<TestCase> testCases = convertTestCaseRequests(request.getTestCases());
        existingData.setTestCases(testCases);
        
        // 保存更新
        CodeQuestionData savedData = codeQuestionDataRepository.save(existingData);
        logger.info("编程题数据更新成功，ID: {}", savedData.getId());
        
        return CodeQuestionDataResponse.fromEntity(savedData);
    }
    
    /**
     * 删除编程题数据
     */
    public void deleteCodeQuestionData(String codeDataId, String userId) {
        logger.info("删除编程题数据，ID: {}, 用户ID: {}", codeDataId, userId);
        
        CodeQuestionData codeData = findCodeQuestionDataByIdAndUserId(codeDataId, userId);
        codeQuestionDataRepository.delete(codeData);
        
        logger.info("编程题数据删除成功，ID: {}", codeDataId);
    }
    
    // =========================== 查询操作 ===========================
    
    /**
     * 根据ID获取编程题数据
     */
    @Transactional(readOnly = true)
    public CodeQuestionDataResponse getCodeQuestionDataById(String codeDataId, String userId) {
        CodeQuestionData codeData = findCodeQuestionDataByIdAndUserId(codeDataId, userId);
        return CodeQuestionDataResponse.fromEntity(codeData);
    }
    
    /**
     * 根据知识点ID获取编程题数据
     */
    @Transactional(readOnly = true)
    public CodeQuestionDataResponse getCodeQuestionDataByKnowledgeId(String knowledgeId, String userId) {
        CodeQuestionData codeData = codeQuestionDataRepository.findByKnowledgeIdAndUserId(knowledgeId, userId)
                .orElseThrow(() -> new RuntimeException("编程题数据不存在"));
        return CodeQuestionDataResponse.fromEntity(codeData);
    }
    
    /**
     * 获取用户所有编程题数据
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getAllCodeQuestionDataByUser(String userId) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findAllByUserId(userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据编程语言获取数据
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getCodeQuestionDataByLanguage(String userId, String programmingLanguage) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findByProgrammingLanguageAndUserId(programmingLanguage, userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据难度等级获取数据
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getCodeQuestionDataByDifficulty(String userId, Integer difficultyLevel) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findByDifficultyLevelAndUserId(difficultyLevel, userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据难度范围获取数据
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getCodeQuestionDataByDifficultyRange(String userId, Integer minLevel, Integer maxLevel) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findByDifficultyRangeAndUserId(minLevel, maxLevel, userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据分值范围获取数据
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getCodeQuestionDataByPointsRange(String userId, Integer minPoints, Integer maxPoints) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findByPointsRangeAndUserId(minPoints, maxPoints, userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据分类ID获取编程题数据
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getCodeQuestionDataByCategory(String userId, String categoryId) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findByCategoryIdAndUserId(categoryId, userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取编程题数据
     */
    @Transactional(readOnly = true)
    public Page<CodeQuestionDataResponse> getCodeQuestionDataWithPagination(String userId, Pageable pageable) {
        Page<CodeQuestionData> codeDataPage = codeQuestionDataRepository.findByUserIdWithPagination(userId, pageable);
        return codeDataPage.map(CodeQuestionDataResponse::fromEntity);
    }
    
    /**
     * 根据复合条件分页查询
     */
    @Transactional(readOnly = true)
    public Page<CodeQuestionDataResponse> getCodeQuestionDataByComplexConditions(String userId, 
                                                                                 String categoryId, 
                                                                                 String programmingLanguage, 
                                                                                 Integer difficultyLevel, 
                                                                                 Integer minPoints, 
                                                                                 Integer maxPoints, 
                                                                                 Pageable pageable) {
        Page<CodeQuestionData> codeDataPage = codeQuestionDataRepository.findByComplexConditions(
                userId, categoryId, programmingLanguage, difficultyLevel, minPoints, maxPoints, pageable);
        return codeDataPage.map(CodeQuestionDataResponse::fromEntity);
    }
    
    /**
     * 根据编程语言和难度联合查询
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getCodeQuestionDataByLanguageAndDifficulty(String userId, 
                                                                                     String programmingLanguage, 
                                                                                     Integer difficultyLevel) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findByLanguageAndDifficultyAndUserId(
                programmingLanguage, difficultyLevel, userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    // =========================== 搜索功能 ===========================
    
    /**
     * 根据关键词搜索编程题数据
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> searchCodeQuestionData(String userId, String keyword) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.searchByKeyword(userId, keyword);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据代码内容搜索编程题
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> searchCodeQuestionDataByCodeContent(String userId, String keyword) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.searchByCodeContent(userId, keyword);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据提示内容搜索编程题
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> searchCodeQuestionDataByHints(String userId, String keyword) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.searchByHints(userId, keyword);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    // =========================== 统计分析 ===========================
    
    /**
     * 获取编程题数据统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCodeQuestionDataStatistics(String userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总数统计
        long totalCount = codeQuestionDataRepository.countByUserId(userId);
        statistics.put("totalCount", totalCount);
        
        // 按编程语言统计
        List<Object[]> languageStats = codeQuestionDataRepository.countByProgrammingLanguageAndUserId(userId);
        Map<String, Long> programmingLanguageStats = new HashMap<>();
        for (Object[] stat : languageStats) {
            programmingLanguageStats.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("programmingLanguageStats", programmingLanguageStats);
        
        // 按难度等级统计
        List<Object[]> difficultyStats = codeQuestionDataRepository.countByDifficultyLevelAndUserId(userId);
        Map<String, Long> difficultyLevelStats = new HashMap<>();
        for (Object[] stat : difficultyStats) {
            difficultyLevelStats.put("level" + stat[0], (Long) stat[1]);
        }
        statistics.put("difficultyLevelStats", difficultyLevelStats);
        
        // 按分值范围统计
        Object[] pointsStats = codeQuestionDataRepository.countByPointsRangeAndUserId(userId);
        if (pointsStats != null && pointsStats.length >= 3) {
            Map<String, Long> pointsRangeStats = new HashMap<>();
            pointsRangeStats.put("low", (Long) pointsStats[0]);      // ≤20分
            pointsRangeStats.put("medium", (Long) pointsStats[1]);   // 21-50分
            pointsRangeStats.put("high", (Long) pointsStats[2]);     // >50分
            statistics.put("pointsRangeStats", pointsRangeStats);
        }
        
        // 按分类统计
        List<Object[]> categoryStats = codeQuestionDataRepository.countByCategoryAndUserId(userId);
        Map<String, Long> categoryStatsMap = new HashMap<>();
        for (Object[] stat : categoryStats) {
            categoryStatsMap.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("categoryStats", categoryStatsMap);
        
        // 内容完整性统计
        long withInitialCodeCount = codeQuestionDataRepository.countWithInitialCodeByUserId(userId);
        statistics.put("withInitialCodeCount", withInitialCodeCount);
        
        long withHintsCount = codeQuestionDataRepository.countWithHintsByUserId(userId);
        statistics.put("withHintsCount", withHintsCount);
        
        long withStandardAnswerCount = codeQuestionDataRepository.countWithStandardAnswerByUserId(userId);
        statistics.put("withStandardAnswerCount", withStandardAnswerCount);
        
        // 平均数据
        Double avgDifficulty = codeQuestionDataRepository.getAverageDifficultyByUserId(userId);
        statistics.put("averageDifficulty", avgDifficulty != null ? avgDifficulty : 0.0);
        
        Double avgPoints = codeQuestionDataRepository.getAveragePointsByUserId(userId);
        statistics.put("averagePoints", avgPoints != null ? avgPoints : 0.0);
        
        return statistics;
    }
    
    /**
     * 查找测试用例数量最多的编程题
     */
    @Transactional(readOnly = true)
    public List<CodeQuestionDataResponse> getCodeQuestionDataWithMaxTestCases(String userId) {
        List<CodeQuestionData> codeDataList = codeQuestionDataRepository.findMaxTestCasesByUserId(userId);
        return codeDataList.stream()
                .map(CodeQuestionDataResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    // =========================== 私有辅助方法 ===========================
    
    /**
     * 验证创建编程题数据请求
     */
    private void validateCodeQuestionDataRequest(CreateCodeQuestionDataRequest request) {
        if (!request.isValidTestCaseConfiguration()) {
            throw new RuntimeException("测试用例配置不合理：测试用例名称不能重复，排序顺序必须从0开始且连续");
        }
        
        if (!request.isValidTestCasePointsConfiguration()) {
            throw new RuntimeException("测试用例分值配置不合理：测试用例分值总和必须等于题目总分值");
        }
        
        if (!request.hasPublicTestCase()) {
            throw new RuntimeException("测试用例配置不合理：至少需要一个公开测试用例");
        }
        
        if (!request.isValidTestCasePoints()) {
            throw new RuntimeException("测试用例分值配置不合理：所有测试用例分值必须大于0");
        }
    }
    
    /**
     * 验证更新编程题数据请求
     */
    private void validateCodeQuestionDataUpdateRequest(UpdateCodeQuestionDataRequest request) {
        if (!request.isValidTestCaseConfiguration()) {
            throw new RuntimeException("测试用例配置不合理：测试用例名称不能重复，排序顺序必须从0开始且连续");
        }
        
        if (!request.isValidTestCasePointsConfiguration()) {
            throw new RuntimeException("测试用例分值配置不合理：测试用例分值总和必须等于题目总分值");
        }
        
        if (!request.hasPublicTestCase()) {
            throw new RuntimeException("测试用例配置不合理：至少需要一个公开测试用例");
        }
        
        if (!request.isValidTestCasePoints()) {
            throw new RuntimeException("测试用例分值配置不合理：所有测试用例分值必须大于0");
        }
        
        if (!request.isValidSortOrderConfiguration()) {
            throw new RuntimeException("测试用例排序配置不合理：排序顺序必须从0开始且连续");
        }
    }
    
    /**
     * 转换测试用例请求为测试用例实体
     */
    private List<TestCase> convertTestCaseRequests(List<TestCaseRequest> requests) {
        if (requests == null) {
            return new ArrayList<>();
        }
        
        return requests.stream()
                .map(request -> new TestCase(
                        request.getCaseName(),
                        request.getInputData(),
                        request.getExpectedOutput(),
                        request.getIsHidden(),
                        request.getCasePoints(),
                        request.getSortOrder(),
                        request.getDescription()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID和用户ID查找编程题数据（权限控制）
     */
    private CodeQuestionData findCodeQuestionDataByIdAndUserId(String codeDataId, String userId) {
        return codeQuestionDataRepository.findByIdAndUserId(codeDataId, userId)
                .orElseThrow(() -> new RuntimeException("编程题数据不存在或无权限访问"));
    }
    
    /**
     * 生成编程题数据ID
     * 格式：CQDC_{date}_{seq}
     * 优化版本：解决并发安全和线程安全问题
     */
    private synchronized String generateCodeQuestionDataId() {
        // 使用线程安全的日期格式化
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 获取今日已创建的编程题数量（添加synchronized确保原子性）
        long todayCount = getTodayCodeQuestionDataCount();
        
        // 生成序列号（从001开始）
        String sequence = String.format("%03d", todayCount + 1);
        
        return "CQDC_" + today + "_" + sequence;
    }
    
    /**
     * 获取今日创建的编程题数量
     * 优化版本：使用更精确的时间计算
     */
    private long getTodayCodeQuestionDataCount() {
        // 使用LocalDateTime获取更精确的时间范围
        java.time.LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        java.time.LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        long startOfDayMillis = startOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfDayMillis = endOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        
        return codeQuestionDataRepository.countTodayCreated(startOfDayMillis, endOfDayMillis);
    }
} 