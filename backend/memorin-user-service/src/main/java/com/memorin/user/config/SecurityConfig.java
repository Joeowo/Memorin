package com.memorin.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring Security 安全配置
 * 开发阶段暂时禁用认证，方便测试
 * 
 * @author Memorin Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护
            .csrf().disable()
            // 启用CORS支持
            .cors().and()
            // 配置请求授权
            .authorizeRequests()
                // 允许所有actuator端点公开访问
                .antMatchers("/actuator/**").permitAll()
                // 允许所有test端点公开访问
                .antMatchers("/test/**").permitAll()
                // 允许认证接口公开访问
                .antMatchers("/auth/**").permitAll()
                // 允许H2控制台访问
                .antMatchers("/h2-console/**").permitAll()
                // 允许错误页面访问
                .antMatchers("/error").permitAll()
                // 允许API文档访问 - Swagger UI
                .antMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                // 允许API文档访问 - OpenAPI规范
                .antMatchers("/v3/api-docs/**").permitAll()
                // 允许Swagger资源访问
                .antMatchers("/webjars/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            .and()
            // 禁用session管理(用于JWT)
            .sessionManagement().disable()
            // 禁用表单登录
            .formLogin().disable()
            // 禁用HTTP Basic认证
            .httpBasic().disable();

        // 为H2控制台禁用frame options
        http.headers().frameOptions().disable();
    }
} 