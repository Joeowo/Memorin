package com.memorin.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关全局异常处理器
 * 捕获和记录网关转发请求时的异常
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 记录异常信息
        logger.error("网关转发请求时发生异常: {}", ex.getMessage(), ex);
        logger.error("请求路径: {}", exchange.getRequest().getPath());
        logger.error("请求方法: {}", exchange.getRequest().getMethod());
        logger.error("请求头: {}", exchange.getRequest().getHeaders());

        // 构建错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("path", exchange.getRequest().getPath().value());
        errorResponse.put("status", 500);
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "网关转发请求时发生异常: " + ex.getMessage());
        errorResponse.put("requestId", exchange.getRequest().getId());

        String errorJson = "{\"timestamp\":\"" + errorResponse.get("timestamp") + 
                          "\",\"path\":\"" + errorResponse.get("path") + 
                          "\",\"status\":500,\"error\":\"Internal Server Error\"," +
                          "\"message\":\"网关转发请求时发生异常: " + ex.getMessage() + "\"," +
                          "\"requestId\":\"" + errorResponse.get("requestId") + "\"}";

        DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
} 