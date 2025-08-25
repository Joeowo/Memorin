package com.memorin.gateway.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc WebFlux专用配置
 * 解决API网关中SpringDoc与WebFlux的兼容性问题
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-17
 */
@Configuration
public class SpringDocConfig {

    /**
     * 网关API分组配置
     */
    @Bean
    public GroupedOpenApi gatewayApi() {
        return GroupedOpenApi.builder()
                .group("gateway")
                .pathsToMatch("/gateway/**", "/actuator/**")
                .packagesToScan("com.memorin.gateway.controller")
                .build();
    }
} 