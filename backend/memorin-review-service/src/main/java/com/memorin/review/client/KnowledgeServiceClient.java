package com.memorin.review.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * 知识服务HTTP客户端
 * 负责调用knowledge-service的REST API获取知识点数据
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
public class KnowledgeServiceClient {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${memorin.knowledge-service.url:http://localhost:8082}")
    private String knowledgeServiceUrl;

    public KnowledgeServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取所有知识点
     * 
     * @param userId 用户ID
     * @return 知识点列表
     * @throws Exception 调用异常
     */
    public List<Map<String, Object>> getAllKnowledge(String userId) throws Exception {
        String url = knowledgeServiceUrl + "/api/test/knowledge";
        
        try {
            log.debug("[知识服务客户端] 获取所有知识点，URL: {}", url);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object dataObj = responseBody.get("data");
                
                if (dataObj instanceof List) {
                    List<Map<String, Object>> knowledgeList = (List<Map<String, Object>>) dataObj;
                    log.debug("[知识服务客户端] 成功获取 {} 个知识点", knowledgeList.size());
                    return knowledgeList;
                }
            }
            
            log.warn("[知识服务客户端] 获取知识点返回数据格式异常: {}", response.getBody());
            return new ArrayList<>();
            
        } catch (HttpClientErrorException e) {
            log.error("[知识服务客户端] HTTP错误: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("调用知识服务失败: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("[知识服务客户端] 连接错误: {}", e.getMessage());
            throw new Exception("无法连接到知识服务: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("[知识服务客户端] 未知错误", e);
            throw new Exception("获取知识点数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据分类获取知识点
     * 
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 知识点列表
     * @throws Exception 调用异常
     */
    public List<Map<String, Object>> getKnowledgeByCategory(String userId, String categoryId) throws Exception {
        String url = knowledgeServiceUrl + "/api/knowledge/category/" + categoryId;
        
        try {
            log.debug("[知识服务客户端] 根据分类获取知识点，分类ID: {}, URL: {}", categoryId, url);
            
            HttpHeaders headers = new HttpHeaders();
            // 如果需要认证，这里添加JWT token
            // headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object dataObj = responseBody.get("data");
                
                if (dataObj instanceof List) {
                    List<Map<String, Object>> knowledgeList = (List<Map<String, Object>>) dataObj;
                    log.debug("[知识服务客户端] 成功获取分类 {} 下的 {} 个知识点", categoryId, knowledgeList.size());
                    return knowledgeList;
                }
            }
            
            log.warn("[知识服务客户端] 获取分类知识点返回数据格式异常: {}", response.getBody());
            return new ArrayList<>();
            
        } catch (Exception e) {
            log.error("[知识服务客户端] 获取分类知识点失败", e);
            throw new Exception("获取分类知识点失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取单个知识点详情
     * 
     * @param userId 用户ID
     * @param knowledgeId 知识点ID
     * @return 知识点详情
     * @throws Exception 调用异常
     */
    public Map<String, Object> getKnowledgeById(String userId, String knowledgeId) throws Exception {
        String url = knowledgeServiceUrl + "/api/test/knowledge/" + knowledgeId;
        
        try {
            log.debug("[知识服务客户端] 获取知识点详情，ID: {}, URL: {}", knowledgeId, url);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object dataObj = responseBody.get("data");
                
                if (dataObj instanceof Map) {
                    Map<String, Object> knowledge = (Map<String, Object>) dataObj;
                    log.debug("[知识服务客户端] 成功获取知识点详情: {}", knowledgeId);
                    return knowledge;
                }
            }
            
            log.warn("[知识服务客户端] 获取知识点详情返回数据格式异常: {}", response.getBody());
            return new HashMap<>();
            
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[知识服务客户端] 知识点不存在: {}", knowledgeId);
            throw new Exception("知识点不存在: " + knowledgeId, e);
        } catch (Exception e) {
            log.error("[知识服务客户端] 获取知识点详情失败", e);
            throw new Exception("获取知识点详情失败: " + e.getMessage(), e);
        }
    }

    /**
     * 分页获取知识点
     * 
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页知识点数据
     * @throws Exception 调用异常
     */
    public Map<String, Object> getKnowledgeWithPagination(String userId, int page, int size) throws Exception {
        String url = knowledgeServiceUrl + "/api/knowledge/page?page=" + page + "&size=" + size;
        
        try {
            log.debug("[知识服务客户端] 分页获取知识点，页码: {}, 大小: {}, URL: {}", page, size, url);
            
            HttpHeaders headers = new HttpHeaders();
            // 如果需要认证，这里添加JWT token
            // headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.debug("[知识服务客户端] 成功获取分页知识点数据");
                return responseBody;
            }
            
            log.warn("[知识服务客户端] 分页获取知识点返回数据格式异常: {}", response.getBody());
            return new HashMap<>();
            
        } catch (Exception e) {
            log.error("[知识服务客户端] 分页获取知识点失败", e);
            throw new Exception("分页获取知识点失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查知识服务是否可用
     * 
     * @return 是否可用
     */
    public boolean isServiceAvailable() {
        try {
            String url = knowledgeServiceUrl + "/api/test/info";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            boolean available = response.getStatusCode().is2xxSuccessful();
            log.debug("[知识服务客户端] 服务可用性检查: {}", available);
            return available;
        } catch (Exception e) {
            log.warn("[知识服务客户端] 服务不可用: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取知识服务URL
     * 
     * @return 服务URL
     */
    public String getKnowledgeServiceUrl() {
        return knowledgeServiceUrl;
    }
} 