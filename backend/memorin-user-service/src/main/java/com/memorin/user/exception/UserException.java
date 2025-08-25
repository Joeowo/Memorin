package com.memorin.user.exception;

/**
 * 用户服务自定义异常
 * 定义用户相关的业务异常类型
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
public class UserException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    public UserException(String message) {
        super(message);
        this.errorCode = "USER_ERROR";
        this.httpStatus = 400;
    }
    
    public UserException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }
    
    public UserException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public UserException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }
    
    public UserException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
    
    // 预定义的异常类型
    public static class UserNotFoundException extends UserException {
        public UserNotFoundException(String message) {
            super("USER_NOT_FOUND", message, 404);
        }
    }
    
    public static class UserAlreadyExistsException extends UserException {
        public UserAlreadyExistsException(String message) {
            super("USER_ALREADY_EXISTS", message, 409);
        }
    }
    
    public static class InvalidCredentialsException extends UserException {
        public InvalidCredentialsException(String message) {
            super("INVALID_CREDENTIALS", message, 401);
        }
    }
    
    public static class ValidationException extends UserException {
        public ValidationException(String message) {
            super("VALIDATION_ERROR", message, 400);
        }
    }
    
    public static class AuthenticationException extends UserException {
        public AuthenticationException(String message) {
            super("AUTHENTICATION_ERROR", message, 401);
        }
    }
    
    public static class AuthorizationException extends UserException {
        public AuthorizationException(String message) {
            super("AUTHORIZATION_ERROR", message, 403);
        }
    }
} 