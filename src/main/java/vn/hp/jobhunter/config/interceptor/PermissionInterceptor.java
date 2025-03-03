package vn.hp.jobhunter.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.hp.jobhunter.entity.Permission;
import vn.hp.jobhunter.entity.Role;
import vn.hp.jobhunter.entity.User;
import vn.hp.jobhunter.service.UserService;
import vn.hp.jobhunter.util.SecurityUtil;
import vn.hp.jobhunter.util.error.PermissionException;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path = " + path);
        System.out.println(">>> httpMethod = " + httpMethod);
        System.out.println(">>> requestURI = " + requestURI);

        //Check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (!email.isEmpty()) {
            User u = this.userService.getUserByUsername(email);
            if (u != null) {
                Role r = u.getRole();
                if (r != null) {
                    List<Permission> permissions = r.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    System.out.println(">>> Allow = " + isAllow);
                    if (!isAllow) throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                }
                else throw new PermissionException("Bạn không có quyền truy cập endpoint này");
            }
        }
        return true;
    }
}
