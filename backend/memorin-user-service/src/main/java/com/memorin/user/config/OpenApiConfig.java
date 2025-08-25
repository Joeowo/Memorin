package com.memorin.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Memorin用户服务 - SpringDoc OpenAPI配置
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-17
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Memorin用户服务 API")
                        .description("Memorin智能知识复习系统 - 用户服务\n\n" +
                                "🔐 **核心功能**: 用户认证、授权和用户管理\n" +
                                "🎯 **主要特性**: JWT身份验证、角色权限管理、用户信息维护\n" +
                                "📊 **API分类**: 认证接口、用户管理接口、权限控制接口\n\n" +
                                "### 🔑 认证流程\n" +
                                "1. **注册**: 创建新用户账户\n" +
                                "2. **登录**: 验证凭据并获取JWT令牌\n" +
                                "3. **令牌验证**: 使用JWT访问受保护资源\n" +
                                "4. **刷新令牌**: 延长会话有效期\n\n" +
                                "### 👤 用户管理\n" +
                                "- 用户信息查询和更新\n" +
                                "- 密码修改和重置\n" +
                                "- 用户状态管理")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Memorin用户服务团队")
                                .email("user-service@memorin.com")
                                .url("https://github.com/memorin-demo/user-service"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8081")
                                .description("开发环境 - 用户服务"),
                        new Server()
                                .url("https://api.memorin.com/user")
                                .description("生产环境 - 用户服务")));
    }
} 