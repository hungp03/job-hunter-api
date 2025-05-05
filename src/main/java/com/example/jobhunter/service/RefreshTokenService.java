package com.example.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.example.jobhunter.entity.RefreshToken;
import com.example.jobhunter.entity.User;
import com.example.jobhunter.repository.RefreshTokenRepository;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtil securityUtil;

    public RefreshToken validateToken(String refreshToken, String deviceKey) {
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        Optional<RefreshToken> refreshTokenOptional = this.refreshTokenRepository.findByTokenAndDeviceKey(refreshToken, deviceKey);
        if (refreshTokenOptional.isEmpty() || !refreshTokenOptional.get().getUser().getEmail().equals(email)) {
            throw new IdInvalidException("Refresh token không hợp lệ hoặc không khớp với thiết bị");
        }
        return refreshTokenOptional.get();
    }

    public void save(RefreshToken refreshToken) {
        this.refreshTokenRepository.save(refreshToken);
    }

    public void storeRefreshToken(User user, String refreshToken, String userAgent, String deviceKey) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserIdAndDeviceKey(user.getId(), deviceKey);
        if (existingToken.isPresent()) {
            // Cập nhật refreshToken mới
            RefreshToken userToken = existingToken.get();
            userToken.setToken(refreshToken);
            userToken.setDeviceInfo(userAgent);
            refreshTokenRepository.save(userToken);
        } else {
            // Tạo mới UserToken
            RefreshToken newUserToken = new RefreshToken();
            newUserToken.setUser(user);
            newUserToken.setToken(refreshToken);
            newUserToken.setDeviceInfo(userAgent);
            newUserToken.setDeviceKey(deviceKey);
            refreshTokenRepository.save(newUserToken);
        }
    }

    public RefreshToken findByEmailAndDeviceKey(String email, String deviceKey) {
        Optional<RefreshToken> refreshTokenOptional = this.refreshTokenRepository.findByUserEmailAndDeviceKey(email, deviceKey);
        if (refreshTokenOptional.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy phiên đăng nhập trên thiết bị này.");
        }
        return refreshTokenOptional.get();
    }

    public void deleteToken(RefreshToken userToken) {
        this.refreshTokenRepository.delete(userToken);
    }
}
