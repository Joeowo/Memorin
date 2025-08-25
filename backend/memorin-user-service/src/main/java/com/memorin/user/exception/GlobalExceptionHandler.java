package com.memorin.user.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理所有异常并返回标准化的错误响应
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理用户服务自定义异常
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, Object>> handleUserException(UserException ex, WebRequest request) {
        logger.error("用户服务异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = buildErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            ex.getHttpStatus(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    /**
     * 处理参数验证异常 (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        logger.error("参数验证失败: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing + "; " + replacement
            ));
        
        Map<String, Object> errorResponse = buildErrorResponse(
            "VALIDATION_ERROR",
            "请求参数验证失败",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false)
        );
        errorResponse.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException ex, WebRequest request) {
        logger.error("数据绑定失败: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing + "; " + replacement
            ));
        
        Map<String, Object> errorResponse = buildErrorResponse(
            "BIND_ERROR",
            "数据绑定失败",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false)
        );
        errorResponse.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        logger.error("约束验证失败: {}", ex.getMessage());
        
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> fieldErrors = violations.stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (existing, replacement) -> existing + "; " + replacement
            ));
        
        Map<String, Object> errorResponse = buildErrorResponse(
            "CONSTRAINT_VIOLATION",
            "约束验证失败",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false)
        );
        errorResponse.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.error("非法参数异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = buildErrorResponse(
            "ILLEGAL_ARGUMENT",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false)
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 处理其他运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        logger.error("运行时异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = buildErrorResponse(
            "RUNTIME_ERROR",
            "系统运行时异常",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        logger.error("系统异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = buildErrorResponse(
            "SYSTEM_ERROR",
            "系统内部错误",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 构建标准错误响应
     */
    private Map<String, Object> buildErrorResponse(String errorCode, String message, 
                                                  int status, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        errorResponse.put("success", false);
        return errorResponse;
    }
} 