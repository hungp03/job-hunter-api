package com.example.jobhunter.config.interceptor;

import com.example.jobhunter.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public List<WhitelistRule> permissionWhitelist() {
        return List.of(
                new WhitelistRule("/", null),
                new WhitelistRule("/api/auth/**", null),
                new WhitelistRule("/storage/**", null),
                new WhitelistRule("/swagger-ui/**", null),
                new WhitelistRule("/v3/api-docs/**", null),
                new WhitelistRule("/api/companies/**", "GET"),
                new WhitelistRule("/api/jobs/**", "GET"),
                new WhitelistRule("/api/skills/**", "GET"),
                new WhitelistRule("/api/resumes/**", "GET"),
                new WhitelistRule("/api/subscribers/**", "GET")
        );
    }

    @Bean
    public PermissionInterceptor getPermissionInterceptor(List<WhitelistRule> whitelist, UserService userService) {
        return new PermissionInterceptor(whitelist, userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getPermissionInterceptor(permissionWhitelist(), null));
    }
}
