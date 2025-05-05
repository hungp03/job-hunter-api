package com.example.jobhunter.config.interceptor;

import com.example.jobhunter.entity.Permission;
import com.example.jobhunter.entity.Role;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.service.UserService;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.error.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {
    private final List<WhitelistRule> whitelist;
    private final UserService userService;

    private static final AntPathMatcher matcher = new AntPathMatcher();

    public PermissionInterceptor(List<WhitelistRule> whitelist, UserService userService) {
        this.whitelist = whitelist;
        this.userService = userService;
    }

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();

        // Check whitelist
        boolean isWhitelisted = whitelist.stream().anyMatch(rule ->
                matcher.match(rule.pathPattern(), path) &&
                        (rule.httpMethod() == null || rule.httpMethod().equalsIgnoreCase(method))
        );

        if (isWhitelisted) return true;

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (!email.isEmpty()) {
            User user = userService.getUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllowed = permissions.stream().anyMatch(p ->
                            p.getApiPath().equals(path) && p.getMethod().equalsIgnoreCase(method));
                    if (!isAllowed) throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                } else {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                }
            }
        }

        return true;
    }
}
