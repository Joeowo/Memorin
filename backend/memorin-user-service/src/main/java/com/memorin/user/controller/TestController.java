package com.memorin.user.controller;

import com.memorin.user.entity.User;
import com.memorin.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试控制器 - 验证用户服务基础功能
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@RestController
@RequestMapping("/test")
@Tag(name = "系统测试", description = "用户服务测试接口，用于验证服务基础功能、API调用和数据操作")
public class TestController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private UserService userService;

    /**
     * 基础健康检查
     */
    @GetMapping("/ping")
    @Operation(summary = "健康检查", description = "检查用户服务是否正常运行，返回服务状态和基本信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "服务运行正常")
    })
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "用户服务运行正常");
        response.put("service", applicationName);
        response.put("port", serverPort);
        response.put("timestamp", LocalDateTime.now());
        response.put("userCount", userService.count());
        return ResponseEntity.ok(response);
    }

    /**
     * 服务信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", applicationName);
        response.put("port", serverPort);
        response.put("java.version", System.getProperty("java.version"));
        response.put("os.name", System.getProperty("os.name"));
        response.put("os.version", System.getProperty("os.version"));
        response.put("description", "Memorin用户服务 - 处理用户认证和管理");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * 路径参数测试
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("message", "获取用户信息");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * 查询参数测试
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("keyword", keyword);
        response.put("page", page);
        response.put("size", size);
        response.put("message", "搜索用户");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * POST请求测试
     */
    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", data);
        response.put("message", "数据回显成功");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // ==================== 用户实体测试接口 ====================

    /**
     * 创建测试用户
     */
    @PostMapping("/user/create")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> userData) {
        try {
            String username = userData.get("username");
            String email = userData.get("email");
            String password = userData.getOrDefault("password", "test123456");

            User user = userService.createUser(username, email, password);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户创建成功");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("status", user.getStatus());
            response.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取用户列表成功");
        response.put("count", users.size());
        response.put("users", users);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/user/id/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("success", true);
            response.put("message", "获取用户成功");
            response.put("user", user);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在: ID=" + id);
        }
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/user/username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userService.findByUsername(username);
        
        Map<String, Object> response = new HashMap<>();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("success", true);
            response.put("message", "获取用户成功");
            response.put("user", user);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在: " + username);
        }
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 数据库连接测试
     */
    @GetMapping("/database/test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        try {
            long userCount = userService.count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "数据库连接正常");
            response.put("userCount", userCount);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "数据库连接失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 密码加密验证测试
     */
    @PostMapping("/password/verify")
    public ResponseEntity<Map<String, Object>> testPasswordVerification(@RequestBody Map<String, String> data) {
        try {
            String plainPassword = data.get("password");
            if (plainPassword == null || plainPassword.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 使用BCryptPasswordEncoder进行密码加密和验证
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            
            // 测试密码加密
            String encodedPassword = encoder.encode(plainPassword);
            
            // 测试密码验证
            boolean isValid = encoder.matches(plainPassword, encodedPassword);
            boolean isInvalid = encoder.matches("wrongpassword", encodedPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密码加密验证测试完成");
            response.put("plainPassword", plainPassword);
            response.put("encodedPassword", encodedPassword);
            response.put("correctPasswordMatch", isValid);
            response.put("wrongPasswordMatch", isInvalid);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "密码验证测试失败: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 