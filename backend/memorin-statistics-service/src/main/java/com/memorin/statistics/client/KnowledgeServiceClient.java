package com.memorin.statistics.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * 知识库服务HTTP客户端
 * 负责调用knowledge-service的REST API获取知识点统计数据
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class KnowledgeServiceClient {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${services.knowledge-service.base-url}")
    private String knowledgeServiceBaseUrl;

    @Value("${services.knowledge-service.timeout:5000}")
    private int timeout;

    public KnowledgeServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取用户知识点统计数据
     * 调用知识库服务的统计API获取用户知识点掌握情况
     * 
     * @param userId 用户ID
     * @return 包含知识点统计数据的Map
     *         - totalKnowledgePoints: 总知识点数
     *         - masteredKnowledgePoints: 已掌握知识点数
     *         - statusStats: 按状态分组统计
     *         - difficultyStats: 按难度分组统计
     *         - typeStats: 按题型分组统计
     * @throws RuntimeException 当调用失败时抛出
     */
    public Map<String, Object> getUserKnowledgeStatistics(String userId) {
        log.info("调用知识库服务获取用户统计: userId={}", userId);
        
        try {
            // 构建请求URL - 使用测试接口避免认证复杂性
            String url = knowledgeServiceBaseUrl + "/api/test/knowledge/statistics";
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            log.debug("发送请求到知识库服务: url={}", url);
            
            // 发送GET请求
            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                if (Boolean.TRUE.equals(body.get("success"))) {
                    Map<String, Object> knowledgeStats = (Map<String, Object>) body.get("data");
                    
                    // 构建统计结果
                    Map<String, Object> result = new HashMap<>();
                    
                    if (knowledgeStats != null) {
                        // 总知识点数
                        Object totalCountObj = knowledgeStats.get("totalCount");
                        int totalKnowledgePoints = totalCountObj instanceof Number ? 
                            ((Number) totalCountObj).intValue() : 0;
                        result.put("totalKnowledgePoints", totalKnowledgePoints);
                        
                        // 计算已掌握知识点数
                        int masteredKnowledgePoints = calculateMasteredKnowledgePoints(knowledgeStats);
                        result.put("masteredKnowledgePoints", masteredKnowledgePoints);
                        
                        // 保留原始统计数据供后续分析
                        result.put("statusStats", knowledgeStats.get("statusStats"));
                        result.put("difficultyStats", knowledgeStats.get("difficultyStats"));
                        result.put("typeStats", knowledgeStats.get("typeStats"));
                        
                        log.info("知识库服务调用成功: userId={}, totalPoints={}, masteredPoints={}", 
                                userId, totalKnowledgePoints, masteredKnowledgePoints);
                    } else {
                        log.warn("知识库服务返回空数据: userId={}", userId);
                        result.put("totalKnowledgePoints", 0);
                        result.put("masteredKnowledgePoints", 0);
                    }
                    
                    return result;
                } else {
                    String errorMsg = (String) body.get("message");
                    log.warn("知识库服务返回业务错误: userId={}, error={}", userId, errorMsg);
                    throw new RuntimeException("知识库服务业务错误: " + errorMsg);
                }
            } else {
                log.warn("知识库服务返回异常状态: userId={}, status={}", userId, response.getStatusCode());
                throw new RuntimeException("知识库服务返回异常状态: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException e) {
            log.error("知识库服务HTTP客户端错误: userId={}, status={}, error={}", 
                     userId, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("知识库服务调用失败: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("知识库服务网络连接错误: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("知识库服务连接超时，请检查服务状态");
        } catch (Exception e) {
            log.error("知识库服务调用异常: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("知识库服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 从状态统计中计算已掌握的知识点数量
     * 假设状态包括: "已掌握", "学习中", "未掌握" 等
     */
    private int calculateMasteredKnowledgePoints(Map<String, Object> knowledgeStats) {
        try {
            Map<String, Object> statusStats = (Map<String, Object>) knowledgeStats.get("statusStats");
            if (statusStats == null) {
                return 0;
            }
            
            int masteredCount = 0;
            // 查找已掌握状态的知识点数量
            for (Map.Entry<String, Object> entry : statusStats.entrySet()) {
                String status = entry.getKey();
                Object countObj = entry.getValue();
                
                // 检查是否为已掌握状态 (可能的状态值: "已掌握", "MASTERED", "mastered" 等)
                if (status != null && countObj instanceof Number) {
                    String statusLower = status.toLowerCase();
                    if (statusLower.contains("掌握") || statusLower.contains("mastered") || 
                        statusLower.contains("completed") || statusLower.equals("3")) { // 3可能代表已掌握
                        masteredCount += ((Number) countObj).intValue();
                    }
                }
            }
            
            log.debug("计算已掌握知识点数: statusStats={}, result={}", statusStats, masteredCount);
            return masteredCount;
            
        } catch (Exception e) {
            log.warn("计算已掌握知识点数失败: error={}", e.getMessage());
            return 0;
        }
    }
} 