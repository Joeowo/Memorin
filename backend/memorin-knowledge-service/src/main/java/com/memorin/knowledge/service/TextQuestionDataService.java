package com.memorin.knowledge.service;

import com.memorin.knowledge.dto.request.CreateTextQuestionDataRequest;
import com.memorin.knowledge.dto.request.UpdateTextQuestionDataRequest;
import com.memorin.knowledge.dto.request.ScoringPointRequest;
import com.memorin.knowledge.dto.response.TextQuestionDataResponse;
import com.memorin.knowledge.entity.TextQuestionData;
import com.memorin.knowledge.entity.ScoringPoint;
import com.memorin.knowledge.entity.BaseKnowledge;
import com.memorin.knowledge.repository.TextQuestionDataRepository;
import com.memorin.knowledge.repository.BaseKnowledgeRepository;
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
 * 文本题数据业务逻辑服务
 * 包含文本题数据的CRUD操作、ID生成、知识点验证等核心业务逻辑
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@Transactional
public class TextQuestionDataService {

    private static final Logger logger = LoggerFactory.getLogger(TextQuestionDataService.class);

    @Autowired
    private TextQuestionDataRepository textQuestionDataRepository;

    @Autowired
    private BaseKnowledgeRepository baseKnowledgeRepository;

    /**
     * 创建新文本题数据
     */
    public TextQuestionDataResponse createTextQuestionData(CreateTextQuestionDataRequest request, String userId) {
        logger.info("创建文本题数据，用户ID: {}, 知识点ID: {}, 题型: {}", userId, request.getKnowledgeId(), request.getTextType());

        // 1. 验证知识点是否存在且为text类型
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(
            request.getKnowledgeId(), userId, true)
            .orElseThrow(() -> new IllegalArgumentException("知识点不存在或已被删除"));

        if (!"text".equals(knowledge.getType())) {
            throw new IllegalArgumentException("只能为文本类型的知识点创建文本题数据");
        }

        // 2. 检查该知识点是否已有文本题数据
        if (textQuestionDataRepository.existsByKnowledgeId(request.getKnowledgeId())) {
            throw new IllegalArgumentException("该知识点已存在文本题数据");
        }

        // 3. 验证请求数据合法性
        validateTextQuestionDataRequest(request);

        // 4. 生成文本题数据ID
        String textQuestionDataId = generateTextQuestionDataId();

        // 5. 创建文本题数据实体
        TextQuestionData textData = new TextQuestionData();
        textData.setId(textQuestionDataId);
        textData.setKnowledgeId(request.getKnowledgeId());
        textData.setTextType(request.getTextType());
        textData.setAnswer(request.getAnswer());
        textData.setValidationMode(request.getValidationMode());
        textData.setAlternativeAnswerList(request.getAlternativeAnswers());
        textData.setCaseSensitive(request.getCaseSensitive() != null ? request.getCaseSensitive() : false);
        textData.setRegexPattern(request.getRegexPattern());
        textData.setMaxWordCount(request.getMaxWordCount());
        textData.setMinWordCount(request.getMinWordCount());

        // 6. 处理评分要点（仅对问答题）
        if (request.isEssayQuestion() && request.getScoringPoints() != null) {
            List<ScoringPoint> scoringPoints = convertScoringPointRequests(request.getScoringPoints());
            textData.setScoringPoints(scoringPoints);
        }

        // 7. 保存到数据库
        TextQuestionData savedTextData = textQuestionDataRepository.save(textData);
        logger.info("成功创建文本题数据，ID: {}, 关联知识点: {}", savedTextData.getId(), savedTextData.getKnowledgeId());

        return TextQuestionDataResponse.fromEntity(savedTextData);
    }

    /**
     * 更新文本题数据
     */
    public TextQuestionDataResponse updateTextQuestionData(String textDataId, UpdateTextQuestionDataRequest request, String userId) {
        logger.info("更新文本题数据，ID: {}, 用户ID: {}", textDataId, userId);

        // 1. 查找文本题数据并验证权限
        TextQuestionData textData = findTextQuestionDataByIdAndUserId(textDataId, userId);

        // 2. 验证请求数据合法性
        validateTextQuestionDataUpdateRequest(request);

        // 3. 更新基本信息
        textData.setTextType(request.getTextType());
        textData.setAnswer(request.getAnswer());
        textData.setValidationMode(request.getValidationMode());
        textData.setAlternativeAnswerList(request.getAlternativeAnswers());
        textData.setCaseSensitive(request.getCaseSensitive() != null ? request.getCaseSensitive() : false);
        textData.setRegexPattern(request.getRegexPattern());
        textData.setMaxWordCount(request.getMaxWordCount());
        textData.setMinWordCount(request.getMinWordCount());

        // 4. 更新评分要点
        if (request.isEssayQuestion() && request.getScoringPoints() != null) {
            List<ScoringPoint> scoringPoints = convertScoringPointRequests(request.getScoringPoints());
            textData.setScoringPoints(scoringPoints);
        } else {
            // 如果改为填空题或没有评分要点，清空评分要点
            textData.setScoringPoints(new ArrayList<>());
        }

        // 5. 保存更新
        TextQuestionData updatedTextData = textQuestionDataRepository.save(textData);
        logger.info("成功更新文本题数据，ID: {}", updatedTextData.getId());

        return TextQuestionDataResponse.fromEntity(updatedTextData);
    }

    /**
     * 删除文本题数据
     */
    public void deleteTextQuestionData(String textDataId, String userId) {
        logger.info("删除文本题数据，ID: {}, 用户ID: {}", textDataId, userId);

        // 1. 查找并验证权限
        TextQuestionData textData = findTextQuestionDataByIdAndUserId(textDataId, userId);

        // 2. 执行删除
        textQuestionDataRepository.delete(textData);
        logger.info("成功删除文本题数据，ID: {}", textDataId);
    }

    /**
     * 根据ID获取文本题数据详情
     */
    @Transactional(readOnly = true)
    public TextQuestionDataResponse getTextQuestionDataById(String textDataId, String userId) {
        TextQuestionData textData = findTextQuestionDataByIdAndUserId(textDataId, userId);
        return TextQuestionDataResponse.fromEntity(textData);
    }

    /**
     * 根据知识点ID获取文本题数据
     */
    @Transactional(readOnly = true)
    public TextQuestionDataResponse getTextQuestionDataByKnowledgeId(String knowledgeId, String userId) {
        // 验证知识点权限
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(knowledgeId, userId, true)
            .orElseThrow(() -> new IllegalArgumentException("知识点不存在或已被删除"));

        TextQuestionData textData = textQuestionDataRepository.findByKnowledgeId(knowledgeId)
            .orElseThrow(() -> new IllegalArgumentException("该知识点没有对应的文本题数据"));

        return TextQuestionDataResponse.fromEntity(textData);
    }

    /**
     * 获取用户的所有文本题数据
     */
    @Transactional(readOnly = true)
    public List<TextQuestionDataResponse> getAllTextQuestionDataByUser(String userId) {
        List<TextQuestionData> textDataList = textQuestionDataRepository.findByUserId(userId);
        return textDataList.stream()
            .map(TextQuestionDataResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 根据文本题类型获取数据
     */
    @Transactional(readOnly = true)
    public List<TextQuestionDataResponse> getTextQuestionDataByType(String userId, String textType) {
        List<TextQuestionData> textDataList = textQuestionDataRepository.findByUserIdAndTextType(userId, textType);
        return textDataList.stream()
            .map(TextQuestionDataResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 根据分类获取文本题数据
     */
    @Transactional(readOnly = true)
    public List<TextQuestionDataResponse> getTextQuestionDataByCategory(String userId, String categoryId) {
        List<TextQuestionData> textDataList = textQuestionDataRepository.findByUserIdAndCategoryId(userId, categoryId);
        return textDataList.stream()
            .map(TextQuestionDataResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 分页获取文本题数据
     */
    @Transactional(readOnly = true)
    public Page<TextQuestionDataResponse> getTextQuestionDataWithPagination(String userId, Pageable pageable) {
        Page<TextQuestionData> textDataPage = textQuestionDataRepository.findByUserIdWithPagination(userId, pageable);
        return textDataPage.map(TextQuestionDataResponse::fromEntity);
    }

    /**
     * 复合条件查询文本题数据
     */
    @Transactional(readOnly = true)
    public Page<TextQuestionDataResponse> getTextQuestionDataByComplexConditions(String userId, String categoryId, 
                                                                                String textType, String validationMode, 
                                                                                Pageable pageable) {
        Page<TextQuestionData> textDataPage = textQuestionDataRepository.findByComplexConditions(
            userId, categoryId, textType, validationMode, pageable);
        return textDataPage.map(TextQuestionDataResponse::fromEntity);
    }

    /**
     * 搜索文本题数据
     */
    @Transactional(readOnly = true)
    public List<TextQuestionDataResponse> searchTextQuestionData(String userId, String keyword) {
        List<TextQuestionData> textDataList = textQuestionDataRepository.searchByUserIdAndKeyword(userId, keyword);
        return textDataList.stream()
            .map(TextQuestionDataResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 获取文本题数据统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTextQuestionDataStatistics(String userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总数统计
        long totalCount = textQuestionDataRepository.countByUserId(userId);
        statistics.put("totalCount", totalCount);
        
        // 文本题类型统计
        List<Object[]> textTypeStats = textQuestionDataRepository.countByUserIdAndTextType(userId);
        Map<String, Long> textTypeMap = new HashMap<>();
        for (Object[] stat : textTypeStats) {
            textTypeMap.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("textTypeStats", textTypeMap);
        
        // 验证模式统计
        List<Object[]> validationModeStats = textQuestionDataRepository.countByUserIdAndValidationMode(userId);
        Map<String, Long> validationModeMap = new HashMap<>();
        for (Object[] stat : validationModeStats) {
            validationModeMap.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("validationModeStats", validationModeMap);
        
        // 特殊统计
        long essayWithScoringPoints = textQuestionDataRepository.countEssayQuestionsWithScoringPoints();
        statistics.put("essayWithScoringPointsCount", essayWithScoringPoints);
        
        return statistics;
    }

    /**
     * 验证请求数据合法性
     */
    private void validateTextQuestionDataRequest(CreateTextQuestionDataRequest request) {
        // 验证regex模式
        if (request.needsRegexPattern() && (request.getRegexPattern() == null || request.getRegexPattern().trim().isEmpty())) {
            throw new IllegalArgumentException("当验证模式为regex时，必须提供正则表达式模式");
        }

        // 验证字数限制
        if (request.getMinWordCount() != null && request.getMaxWordCount() != null) {
            if (request.getMinWordCount() > request.getMaxWordCount()) {
                throw new IllegalArgumentException("最小字数要求不能大于最大字数限制");
            }
        }

        // 验证评分要点
        if (request.isEssayQuestion() && request.getScoringPoints() != null && !request.getScoringPoints().isEmpty()) {
            int totalPoints = request.getScoringPoints().stream()
                .mapToInt(ScoringPointRequest::getPoints)
                .sum();
            if (totalPoints > 100) {
                throw new IllegalArgumentException("评分要点总分值不能超过100分");
            }
        }
    }

    /**
     * 验证更新请求数据合法性
     */
    private void validateTextQuestionDataUpdateRequest(UpdateTextQuestionDataRequest request) {
        // 验证regex模式
        if (request.needsRegexPattern() && (request.getRegexPattern() == null || request.getRegexPattern().trim().isEmpty())) {
            throw new IllegalArgumentException("当验证模式为regex时，必须提供正则表达式模式");
        }

        // 验证字数限制
        if (request.getMinWordCount() != null && request.getMaxWordCount() != null) {
            if (request.getMinWordCount() > request.getMaxWordCount()) {
                throw new IllegalArgumentException("最小字数要求不能大于最大字数限制");
            }
        }

        // 验证评分要点
        if (request.isEssayQuestion() && request.getScoringPoints() != null && !request.getScoringPoints().isEmpty()) {
            int totalPoints = request.getScoringPoints().stream()
                .mapToInt(ScoringPointRequest::getPoints)
                .sum();
            if (totalPoints > 100) {
                throw new IllegalArgumentException("评分要点总分值不能超过100分");
            }
        }
    }

    /**
     * 转换评分要点请求为实体
     */
    private List<ScoringPoint> convertScoringPointRequests(List<ScoringPointRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        return requests.stream()
            .map(request -> new ScoringPoint(request.getDescription(), request.getPoints(), request.getKeywords()))
            .collect(Collectors.toList());
    }

    /**
     * 查找文本题数据并验证用户权限
     */
    private TextQuestionData findTextQuestionDataByIdAndUserId(String textDataId, String userId) {
        TextQuestionData textData = textQuestionDataRepository.findById(textDataId)
            .orElseThrow(() -> new IllegalArgumentException("文本题数据不存在"));

        // 通过关联的知识点验证用户权限
        BaseKnowledge knowledge = baseKnowledgeRepository.findByIdAndUserIdAndIsActive(textData.getKnowledgeId(), userId, true)
            .orElseThrow(() -> new IllegalArgumentException("没有权限访问此文本题数据"));

        return textData;
    }

    /**
     * 生成文本题数据ID
     * 格式: TQD_{date}_{seq}
     */
    private String generateTextQuestionDataId() {
        // 生成日期字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        
        // 获取当日已有的文本题数据数量作为序号
        long todayCount = getTodayTextQuestionDataCount() + 1;
        String seq = String.format("%03d", todayCount);
        
        // 组合ID
        return String.format("TQD_%s_%s", dateStr, seq);
    }

    /**
     * 获取今日已创建的文本题数据数量
     */
    private long getTodayTextQuestionDataCount() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todayStr = dateFormat.format(new Date());
        
        // 查询ID包含今日日期的文本题数据数量
        List<TextQuestionData> todayTextData = textQuestionDataRepository.findAll().stream()
            .filter(t -> t.getId().contains(todayStr))
            .collect(Collectors.toList());
            
        return todayTextData.size();
    }
} 