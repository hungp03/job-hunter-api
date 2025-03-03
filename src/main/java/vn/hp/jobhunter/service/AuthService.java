package vn.hp.jobhunter.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.dto.request.ReqLoginDTO;
import vn.hp.jobhunter.dto.response.user.ResLoginDTO;
import vn.hp.jobhunter.entity.User;
import vn.hp.jobhunter.util.SecurityUtil;
import vn.hp.jobhunter.util.error.IdInvalidException;

import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public Map<String, Object> login(ReqLoginDTO loginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername
        // cơ chế của authentication: không lưu mật khẩu nếu login thành công
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Nạp thông tin người đang đăng nhập vào securityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO responseLoginDTO = new ResLoginDTO();
        // Lấy thông tin người dùng trong db thay vì thay đổi spring security
        User currentUserDB = this.userService.getUserByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(), currentUserDB.getName(), currentUserDB.getRole());
            responseLoginDTO.setUser(userLogin);
        }

        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), responseLoginDTO);
        responseLoginDTO.setAccessToken(accessToken);
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), responseLoginDTO);
        // Update RF token vào db
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());
        return Map.of(
                "userInfo", responseLoginDTO,
                "refreshToken", refreshToken
        );
    }

    public void logout() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.isEmpty()) {
            throw new IdInvalidException("Accesstoken không hợp lệ");
        }
        this.userService.updateUserToken(null, email);
    }

    public Map<String, Object> getNewRefreshToken(String refreshToken) {
        if (refreshToken.equals("none")) {
            throw new IdInvalidException("Token không hợp lê. Vui lòng đăng nhập");
        }
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        User currentUser = this.userService.getUserByRFTokenAndEmail(email, refreshToken);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token không hợp lệ");
        }

        // Tạo lại RF token và set cookies
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.getUserByUsername(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
        this.userService.updateUserToken(new_refresh_token, email);
        return Map.of("userInfo", res,
                "refreshToken", refreshToken);
    }

    public ResLoginDTO.UserGetAccount getAccount(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDB = this.userService.getUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());
            userGetAccount.setUser(userLogin);
        }
        return userGetAccount;
    }
}
