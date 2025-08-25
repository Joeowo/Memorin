package com.memorin.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Memorin API网关 - SpringDoc OpenAPI配置
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-17
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Memorin API Gateway")
                        .description("Memorin智能知识复习系统 - API网关服务\n\n" +
                                "🌐 **统一入口**: 所有微服务的统一访问点\n" +
                                "🔀 **路由管理**: 智能路由分发和负载均衡\n" +
                                "🔒 **安全控制**: 统一认证授权和访问控制\n" +
                                "📊 **监控管理**: 请求监控和性能分析\n\n" +
                                "### 🏗️ 微服务架构\n" +
                                "- **用户服务** (8081): 认证授权、用户管理\n" +
                                "- **知识库服务** (8082): 知识点、分类管理\n" +
                                "- **复习服务** (8083): SM-2算法、复习会话\n" +
                                "- **统计服务** (8084): 数据分析、报表生成\n\n" +
                                "### 📋 API路由规则\n" +
                                "- `/api/auth/**` → 用户服务认证\n" +
                                "- `/api/user/**` → 用户管理\n" +
                                "- `/api/knowledge/**` → 知识库管理\n" +
                                "- `/api/review/**` → 复习算法\n" +
                                "- `/api/statistics/**` → 统计分析")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Memorin开发团队")
                                .email("team@memorin.com")
                                .url("https://github.com/memorin-demo"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("开发环境 - API网关"),
                        new Server()
                                .url("https://api.memorin.com")
                                .description("生产环境 - API网关")));
    }
} 