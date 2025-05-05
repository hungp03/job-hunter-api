package com.example.jobhunter.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.jobhunter.dto.request.auth.LoginRequestDTO;
import com.example.jobhunter.dto.request.auth.RegisterRequestDTO;
import com.example.jobhunter.dto.response.AuthResponseDTO;
import com.example.jobhunter.dto.response.user.ResCreateUserDTO;
import com.example.jobhunter.dto.response.user.ResLoginDTO;
import com.example.jobhunter.entity.RefreshToken;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.Utils;
import com.example.jobhunter.util.error.IdInvalidException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;


    public AuthResponseDTO login(LoginRequestDTO loginDTO, String userAgent) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername || cơ chế của authentication: không lưu mật khẩu nếu login thành công
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Nạp thông tin người đang đăng nhập vào securityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy thông tin người dùng trong db thay vì thay đổi spring security
        User user = userService.getUserByUsername(loginDTO.getEmail());
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(), user.getEmail(), user.getName());

        ResLoginDTO responseLoginDTO = new ResLoginDTO();
        responseLoginDTO.setUser(userLogin);

        // Tạo AccessToken
        String accessToken = securityUtil.createAccessToken(authentication.getName(), responseLoginDTO);
        responseLoginDTO.setAccessToken(accessToken);

        // Tạo RefreshToken
        String refreshToken = securityUtil.createRefreshToken(loginDTO.getEmail(), responseLoginDTO);

        // Update RF token vào db
        String deviceKey = Utils.generateDeviceKey(userAgent);
        refreshTokenService.storeRefreshToken(user, refreshToken, userAgent, deviceKey);

        return new AuthResponseDTO(responseLoginDTO, refreshToken, deviceKey);
    }


    public void logout(String deviceKey) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Access token không hợp lệ"));
        RefreshToken userToken = this.refreshTokenService.findByEmailAndDeviceKey(email, deviceKey);
        refreshTokenService.deleteToken(userToken);
    }

    public AuthResponseDTO getNewRefreshToken(String refreshToken, String deviceKey) {
        if ("none".equals(refreshToken) || "none".equals(deviceKey)) {
            throw new IdInvalidException("Token hoặc thông tin thiết bị không hợp lệ.");
        }

        RefreshToken userToken = refreshTokenService.validateToken(refreshToken, deviceKey);
        User user = userToken.getUser();

        ResLoginDTO res = new ResLoginDTO();
        res.setUser(new ResLoginDTO.UserLogin(user.getId(), user.getEmail(), user.getName()));
        res.setAccessToken(securityUtil.createAccessToken(user.getEmail(), res));

        String newRefreshToken = securityUtil.createRefreshToken(user.getEmail(), res);
        userToken.setToken(newRefreshToken);
        refreshTokenService.save(userToken);

        return new AuthResponseDTO(res, newRefreshToken);
    }

    public ResLoginDTO.UserGetAccount getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUserDB = this.userService.getUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userGetAccount.setUser(userLogin);
        }
        return userGetAccount;
    }

    public ResCreateUserDTO register(@Valid RegisterRequestDTO registerRequestDTO) {
        User u = new User();
        u.setEmail(registerRequestDTO.getEmail());
        u.setName(registerRequestDTO.getName());
        u.setPassword(registerRequestDTO.getPassword());
        return userService.handleCreateUser(u);
    }
}
