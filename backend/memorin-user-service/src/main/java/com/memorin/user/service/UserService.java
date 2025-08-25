package com.memorin.user.service;

import com.memorin.user.entity.User;
import com.memorin.user.exception.UserException;
import com.memorin.user.repository.UserRepository;
import com.memorin.user.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 用户名正则表达式：只允许字母、数字、下划线
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * 创建新用户
     */
    public User createUser(String username, String email, String password) {
        // 数据验证
        validateUserData(username, email, password);
        
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserException.UserAlreadyExistsException("用户名已存在: " + username);
        }
        
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException.UserAlreadyExistsException("邮箱已存在: " + email);
        }
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(password);
        
        // 创建用户
        User user = new User(username, email, encodedPassword);
        user = userRepository.save(user);
        
        return user;
    }

    /**
     * 用户认证
     */
    public Map<String, Object> authenticateUser(String username, String password) {
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new UserException.InvalidCredentialsException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UserException.InvalidCredentialsException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (!user.isActive()) {
            throw new UserException.AuthenticationException("用户账户已被禁用");
        }
        
        // 生成JWT令牌
        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId());
        
        // 更新最后登录时间
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // 返回认证结果
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("user", user);
        result.put("token", token);
        
        return result;
    }

    /**
     * 根据ID查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 获取所有用户
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * 更新用户信息
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserException.UserNotFoundException("用户不存在: ID=" + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * 统计用户数量
     */
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }

    /**
     * 验证用户数据
     */
    private void validateUserData(String username, String email, String password) {
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
    }

    /**
     * 验证用户名
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new UserException.ValidationException("用户名不能为空");
        }
        
        if (username.length() < 3 || username.length() > 50) {
            throw new UserException.ValidationException("用户名长度必须在3-50个字符之间");
        }
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new UserException.ValidationException("用户名只能包含字母、数字和下划线");
        }
    }

    /**
     * 验证邮箱
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException.ValidationException("邮箱不能为空");
        }
        
        if (email.length() > 100) {
            throw new UserException.ValidationException("邮箱长度不能超过100个字符");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new UserException.ValidationException("邮箱格式不正确");
        }
    }

    /**
     * 验证密码
     */
    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new UserException.ValidationException("密码不能为空");
        }
        
        if (password.length() < 6 || password.length() > 100) {
            throw new UserException.ValidationException("密码长度必须在6-100个字符之间");
        }
        
        // 密码复杂度验证
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$")) {
            throw new UserException.ValidationException("密码必须包含至少一个小写字母、一个大写字母和一个数字");
        }
    }
} 