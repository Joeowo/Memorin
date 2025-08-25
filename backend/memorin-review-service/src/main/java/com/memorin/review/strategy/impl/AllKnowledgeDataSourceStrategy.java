package com.memorin.review.strategy.impl;

import com.memorin.review.client.KnowledgeServiceClient;
import com.memorin.review.dto.response.QuestionGeneratorResponse.QuestionItem;
import com.memorin.review.strategy.DataSourceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 全部知识点数据源策略实现
 * 从knowledge-service获取用户的所有知识点作为题目来源
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class AllKnowledgeDataSourceStrategy implements DataSourceStrategy {

    private static final Logger log = LoggerFactory.getLogger(AllKnowledgeDataSourceStrategy.class);

    @Autowired
    private KnowledgeServiceClient knowledgeServiceClient;

    @Override
    public String getStrategyName() {
        return "all-knowledge";
    }

    @Override
    public String getDescription() {
        return "获取所有知识点";
    }

    @Override
    public List<QuestionItem> getBaseQuestions(Map<String, Object> params, String userId) throws Exception {
        log.debug("[全部知识点数据源] 开始获取所有知识点，用户: {}, 参数: {}", userId, params);

        try {
            // 1. 检查服务可用性
            if (!knowledgeServiceClient.isServiceAvailable()) {
                throw new Exception("知识服务当前不可用，请稍后重试");
            }

            // 2. 获取所有知识点数据
            List<Map<String, Object>> knowledgeList = knowledgeServiceClient.getAllKnowledge(userId);
            
            if (knowledgeList.isEmpty()) {
                log.warn("[全部知识点数据源] 用户 {} 没有任何知识点数据", userId);
                return new ArrayList<>();
            }

            // 3. 转换为QuestionItem
            List<QuestionItem> questions = new ArrayList<>();
            for (Map<String, Object> knowledge : knowledgeList) {
                try {
                    QuestionItem question = convertToQuestionItem(knowledge);
                    if (question != null) {
                        questions.add(question);
                    }
                } catch (Exception e) {
                    log.warn("[全部知识点数据源] 转换知识点失败: {}, 错误: {}", knowledge.get("id"), e.getMessage());
                }
            }

            log.info("[全部知识点数据源] 成功获取 {} 个知识点，转换为 {} 道题目", 
                    knowledgeList.size(), questions.size());

            return questions;

        } catch (Exception e) {
            log.error("[全部知识点数据源] 获取知识点失败", e);
            throw new Exception("获取知识点数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void validateParams(Map<String, Object> params) throws IllegalArgumentException {
        // 全部知识点策略不需要特殊参数验证
        if (params == null) {
            return;
        }

        // 可选参数：是否包含已删除的知识点
        Object includeDeletedObj = params.get("includeDeleted");
        if (includeDeletedObj != null && !(includeDeletedObj instanceof Boolean)) {
            throw new IllegalArgumentException("includeDeleted 参数必须是布尔值");
        }

        // 可选参数：分页大小
        Object pageSizeObj = params.get("pageSize");
        if (pageSizeObj != null) {
            try {
                int pageSize = convertToInteger(pageSizeObj);
                if (pageSize <= 0 || pageSize > 1000) {
                    throw new IllegalArgumentException("pageSize 参数必须在1-1000之间，当前值: " + pageSize);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("pageSize 参数必须是有效的整数，当前值: " + pageSizeObj);
            }
        }
    }

    @Override
    public Map<String, Class<?>> getSupportedParams() {
        Map<String, Class<?>> params = new HashMap<>();
        params.put("includeDeleted", Boolean.class);    // 是否包含已删除的知识点
        params.put("pageSize", Integer.class);          // 分页大小（如果数据量大）
        return params;
    }

    @Override
    public boolean requiresAuthentication() {
        return false; // 目前使用测试接口，不需要认证
    }

    @Override
    public int getEstimatedQuestionCount(Map<String, Object> params, String userId) {
        try {
            // 这里可以调用一个count接口，但为了简化，先返回-1
            return -1;
        } catch (Exception e) {
            log.warn("[全部知识点数据源] 获取预估数量失败: {}", e.getMessage());
            return -1;
        }
    }

    @Override
    public boolean supportsIncremental() {
        return true; // 支持分页获取
    }

    @Override
    public List<QuestionItem> getQuestionsIncremental(Map<String, Object> params, String userId, 
                                                     int offset, int limit) throws Exception {
        try {
            // 计算页码（从0开始）
            int page = offset / limit;
            
            Map<String, Object> pageData = knowledgeServiceClient.getKnowledgeWithPagination(userId, page, limit);
            Object dataObj = pageData.get("data");
            
            if (dataObj instanceof List) {
                List<Map<String, Object>> knowledgeList = (List<Map<String, Object>>) dataObj;
                
                List<QuestionItem> questions = new ArrayList<>();
                for (Map<String, Object> knowledge : knowledgeList) {
                    try {
                        QuestionItem question = convertToQuestionItem(knowledge);
                        if (question != null) {
                            questions.add(question);
                        }
                    } catch (Exception e) {
                        log.warn("[全部知识点数据源] 增量转换知识点失败: {}, 错误: {}", 
                                knowledge.get("id"), e.getMessage());
                    }
                }
                
                log.debug("[全部知识点数据源] 增量获取：页码={}, 大小={}, 结果={}", page, limit, questions.size());
                return questions;
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            log.error("[全部知识点数据源] 增量获取失败", e);
            throw new Exception("增量获取知识点失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将knowledge-service的知识点数据转换为QuestionItem
     */
    private QuestionItem convertToQuestionItem(Map<String, Object> knowledge) {
        if (knowledge == null || knowledge.isEmpty()) {
            return null;
        }

        QuestionItem question = new QuestionItem();
        
        try {
            // 基本信息
            question.setKnowledgePointId(getString(knowledge, "id"));
            question.setTitle(getString(knowledge, "title"));
            question.setContent(getString(knowledge, "content"));
            question.setQuestionType(getString(knowledge, "questionType", "text"));
            question.setDifficulty(getInteger(knowledge, "difficulty", 3));
            question.setKnowledgeBaseId(getString(knowledge, "knowledgeBaseId"));
            question.setKnowledgeAreaId(getString(knowledge, "knowledgeAreaId"));
            question.setCategory(getString(knowledge, "category"));

            // 处理选择题选项
            if ("choice".equals(question.getQuestionType())) {
                Object choicesObj = knowledge.get("choices");
                if (choicesObj instanceof List) {
                    question.setChoices((List<String>) choicesObj);
                }
            }

            // 答案
            question.setCorrectAnswer(getString(knowledge, "answer"));

            // 标签
            Object tagsObj = knowledge.get("tags");
            if (tagsObj instanceof List) {
                question.setTags((List<String>) tagsObj);
            } else if (tagsObj instanceof String) {
                // 如果tags是字符串，按逗号分割
                String tagsStr = (String) tagsObj;
                if (!tagsStr.trim().isEmpty()) {
                    question.setTags(Arrays.asList(tagsStr.split(",\\s*")));
                }
            }

            // 复习信息（如果存在）
            QuestionItem.ReviewInfo reviewInfo = createReviewInfo(knowledge);
            if (reviewInfo != null) {
                question.setReviewInfo(reviewInfo);
            }

            // 元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", "knowledge-service");
            metadata.put("sourceType", "all-knowledge");
            metadata.put("createdAt", getString(knowledge, "createdAt"));
            metadata.put("updatedAt", getString(knowledge, "updatedAt"));
            question.setMetadata(metadata);

            return question;

        } catch (Exception e) {
            log.error("[全部知识点数据源] 转换QuestionItem失败: {}", knowledge, e);
            return null;
        }
    }

    /**
     * 创建复习信息对象
     */
    private QuestionItem.ReviewInfo createReviewInfo(Map<String, Object> knowledge) {
        try {
            QuestionItem.ReviewInfo reviewInfo = new QuestionItem.ReviewInfo();
            
            // SM-2算法相关字段
            reviewInfo.setEaseFactor(getDouble(knowledge, "easeFactor", 2.5));
            reviewInfo.setInterval(getInteger(knowledge, "intervalDays", 1));
            reviewInfo.setReviewCount(getInteger(knowledge, "reviewCount", 0));
            reviewInfo.setCorrectCount(getInteger(knowledge, "correctCount", 0));

            // 时间字段
            String lastReviewedStr = getString(knowledge, "lastReviewed");
            if (lastReviewedStr != null && !lastReviewedStr.isEmpty()) {
                try {
                    reviewInfo.setLastReviewTime(LocalDateTime.parse(lastReviewedStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    log.debug("[全部知识点数据源] 解析lastReviewed时间失败: {}", lastReviewedStr);
                }
            }

            String nextReviewStr = getString(knowledge, "nextReview");
            if (nextReviewStr != null && !nextReviewStr.isEmpty()) {
                try {
                    reviewInfo.setNextReviewTime(LocalDateTime.parse(nextReviewStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    log.debug("[全部知识点数据源] 解析nextReview时间失败: {}", nextReviewStr);
                }
            }

            return reviewInfo;

        } catch (Exception e) {
            log.debug("[全部知识点数据源] 创建复习信息失败: {}", e.getMessage());
            return null;
        }
    }

    // 辅助方法
    private String getString(Map<String, Object> map, String key) {
        return getString(map, key, null);
    }

    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private Integer getInteger(Map<String, Object> map, String key, Integer defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        try {
            return convertToInteger(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Double getDouble(Map<String, Object> map, String key, Double defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
        } catch (Exception e) {
            // 忽略转换错误
        }
        return defaultValue;
    }

    private int convertToInteger(Object obj) throws NumberFormatException {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else {
            throw new NumberFormatException("无法将 " + obj.getClass().getSimpleName() + " 转换为整数");
        }
    }
} 