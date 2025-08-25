package com.memorin.user.controller;

import com.memorin.user.dto.request.LoginRequest;
import com.memorin.user.dto.request.RegisterRequest;
import com.memorin.user.dto.response.LoginResponse;
import com.memorin.user.entity.User;
import com.memorin.user.exception.UserException;
import com.memorin.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * 处理用户登录、注册等认证相关请求
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "用户认证相关API，包括登录、注册、令牌验证等功能")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "401", description = "登录失败")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 执行认证
            Map<String, Object> authResult = userService.authenticateUser(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if ((Boolean) authResult.get("success")) {
                User user = (User) authResult.get("user");
                String token = (String) authResult.get("token");
                
                LoginResponse response = new LoginResponse(true, "登录成功", token, user);
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse(false, (String) authResult.get("message"));
                return ResponseEntity.status(401).body(response);
            }
            
        } catch (UserException e) {
            // 自定义异常会被全局异常处理器处理
            throw e;
        } catch (Exception e) {
            // 其他异常转换为自定义异常
            throw new UserException.AuthenticationException("登录过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户注册成功"),
        @ApiResponse(responseCode = "400", description = "用户注册失败")
    })
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 创建用户
            User user = userService.createUser(
                registerRequest.getUsername(), 
                registerRequest.getEmail(), 
                registerRequest.getPassword()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户注册成功");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("status", user.getStatus());
            response.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (UserException e) {
            // 自定义异常会被全局异常处理器处理
            throw e;
        } catch (Exception e) {
            // 其他异常转换为自定义异常
            throw new UserException("注册过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 验证令牌
     */
    @PostMapping("/verify")
    @Operation(summary = "验证令牌")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "令牌验证成功"),
        @ApiResponse(responseCode = "400", description = "令牌验证失败")
    })
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> tokenRequest) {
        try {
            String token = tokenRequest.get("token");
            
            if (token == null || token.trim().isEmpty()) {
                throw new UserException.ValidationException("令牌不能为空");
            }
            
            // TODO: 实现令牌验证逻辑
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "令牌验证功能待实现");
            
            return ResponseEntity.ok(response);
            
        } catch (UserException e) {
            // 自定义异常会被全局异常处理器处理
            throw e;
        } catch (Exception e) {
            // 其他异常转换为自定义异常
            throw new UserException("令牌验证过程中发生错误: " + e.getMessage());
        }
    }
} 