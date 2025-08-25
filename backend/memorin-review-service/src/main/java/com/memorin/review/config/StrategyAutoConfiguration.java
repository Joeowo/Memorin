package com.memorin.review.config;

import com.memorin.review.service.QuestionGeneratorService;
import com.memorin.review.strategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 策略自动注册配置类
 * 在Spring Boot启动时自动发现并注册所有策略实现类
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Configuration
public class StrategyAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(StrategyAutoConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private QuestionGeneratorService questionGeneratorService;

    /**
     * 在Spring容器初始化完成后自动注册所有策略
     */
    @PostConstruct
    public void registerAllStrategies() {
        log.info("[策略注册] 开始自动注册所有策略实现类...");

        try {
            // 注册数据源策略
            registerDataSourceStrategies();
            
            // 注册过滤器策略
            registerFilterStrategies();
            
            // 注册排序器策略
            registerSorterStrategies();
            
            // 注册限制器策略
            registerLimiterStrategies();

            log.info("[策略注册] 所有策略注册完成");

        } catch (Exception e) {
            log.error("[策略注册] 策略注册过程中发生错误", e);
            throw new RuntimeException("策略注册失败", e);
        }
    }

    /**
     * 注册数据源策略
     */
    private void registerDataSourceStrategies() {
        Map<String, DataSourceStrategy> strategies = applicationContext.getBeansOfType(DataSourceStrategy.class);
        
        log.info("[策略注册] 发现 {} 个数据源策略", strategies.size());
        
        for (DataSourceStrategy strategy : strategies.values()) {
            try {
                questionGeneratorService.registerDataSourceStrategy(strategy);
                log.debug("[策略注册] 数据源策略注册成功: {} - {}", 
                         strategy.getStrategyName(), strategy.getDescription());
            } catch (Exception e) {
                log.error("[策略注册] 数据源策略注册失败: {} - {}", 
                         strategy.getStrategyName(), e.getMessage(), e);
            }
        }
    }

    /**
     * 注册过滤器策略
     */
    private void registerFilterStrategies() {
        Map<String, FilterStrategy> strategies = applicationContext.getBeansOfType(FilterStrategy.class);
        
        log.info("[策略注册] 发现 {} 个过滤器策略", strategies.size());
        
        for (FilterStrategy strategy : strategies.values()) {
            try {
                questionGeneratorService.registerFilterStrategy(strategy);
                log.debug("[策略注册] 过滤器策略注册成功: {} - {}", 
                         strategy.getStrategyName(), strategy.getDescription());
            } catch (Exception e) {
                log.error("[策略注册] 过滤器策略注册失败: {} - {}", 
                         strategy.getStrategyName(), e.getMessage(), e);
            }
        }
    }

    /**
     * 注册排序器策略
     */
    private void registerSorterStrategies() {
        Map<String, SorterStrategy> strategies = applicationContext.getBeansOfType(SorterStrategy.class);
        
        log.info("[策略注册] 发现 {} 个排序器策略", strategies.size());
        
        for (SorterStrategy strategy : strategies.values()) {
            try {
                questionGeneratorService.registerSorterStrategy(strategy);
                log.debug("[策略注册] 排序器策略注册成功: {} - {}", 
                         strategy.getStrategyName(), strategy.getDescription());
            } catch (Exception e) {
                log.error("[策略注册] 排序器策略注册失败: {} - {}", 
                         strategy.getStrategyName(), e.getMessage(), e);
            }
        }
    }

    /**
     * 注册限制器策略
     */
    private void registerLimiterStrategies() {
        Map<String, LimiterStrategy> strategies = applicationContext.getBeansOfType(LimiterStrategy.class);
        
        log.info("[策略注册] 发现 {} 个限制器策略", strategies.size());
        
        for (LimiterStrategy strategy : strategies.values()) {
            try {
                questionGeneratorService.registerLimiterStrategy(strategy);
                log.debug("[策略注册] 限制器策略注册成功: {} - {}", 
                         strategy.getStrategyName(), strategy.getDescription());
            } catch (Exception e) {
                log.error("[策略注册] 限制器策略注册失败: {} - {}", 
                         strategy.getStrategyName(), e.getMessage(), e);
            }
        }
    }
} 