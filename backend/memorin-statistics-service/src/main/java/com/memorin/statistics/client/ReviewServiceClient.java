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
 * 复习服务HTTP客户端
 * 负责调用review-service的REST API获取复习统计数据
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component
public class ReviewServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${services.review-service.base-url}")
    private String reviewServiceBaseUrl;

    @Value("${services.review-service.timeout:5000}")
    private int timeout;

    public ReviewServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取用户复习统计数据
     * 调用复习服务的错题统计API获取用户学习相关数据
     * 
     * @param userId 用户ID
     * @param days 统计天数
     * @return 包含复习统计数据的Map
     *         - mistakeStats: 错题统计数据
     *         - totalMistakes: 总错题数
     *         - resolutionRate: 错题解决率
     *         - averageAccuracyRate: 平均正确率
     *         - recentMistakes30Days: 近30天错题数
     * @throws RuntimeException 当调用失败时抛出
     */
    public Map<String, Object> getUserReviewStatistics(String userId, int days) {
        log.info("调用复习服务获取用户统计: userId={}, days={}", userId, days);
        
        try {
            // 构建请求URL - 调用错题统计接口
            String url = reviewServiceBaseUrl + "/api/mistakes/user/" + userId + "/statistics";
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            log.debug("发送请求到复习服务: url={}", url);
            
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
                    Map<String, Object> mistakeStats = (Map<String, Object>) body.get("data");
                    
                    // 构建统计结果
                    Map<String, Object> reviewStats = new HashMap<>();
                    reviewStats.put("mistakeStats", mistakeStats);
                    
                    // 提取关键指标
                    if (mistakeStats != null) {
                        reviewStats.put("totalMistakes", mistakeStats.get("totalMistakes"));
                        reviewStats.put("resolutionRate", mistakeStats.get("resolutionRate"));
                        reviewStats.put("averageAccuracyRate", mistakeStats.get("averageAccuracyRate"));
                        reviewStats.put("recentMistakes30Days", mistakeStats.get("recentMistakes30Days"));
                        
                        log.info("复习服务调用成功: userId={}, totalMistakes={}, resolutionRate={}", 
                                userId, mistakeStats.get("totalMistakes"), mistakeStats.get("resolutionRate"));
                    }
                    
                    return reviewStats;
                } else {
                    String errorMsg = (String) body.get("message");
                    log.warn("复习服务返回业务错误: userId={}, error={}", userId, errorMsg);
                    throw new RuntimeException("复习服务业务错误: " + errorMsg);
                }
            } else {
                log.warn("复习服务返回异常状态: userId={}, status={}", userId, response.getStatusCode());
                throw new RuntimeException("复习服务返回异常状态: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException e) {
            log.error("复习服务HTTP客户端错误: userId={}, status={}, error={}", 
                     userId, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("复习服务调用失败: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("复习服务网络连接错误: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("复习服务连接超时，请检查服务状态");
        } catch (Exception e) {
            log.error("复习服务调用异常: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("复习服务调用失败: " + e.getMessage());
        }
    }
} 