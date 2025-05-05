package com.example.jobhunter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jobhunter.dto.request.auth.RegisterRequestDTO;
import com.example.jobhunter.dto.response.AuthResponseDTO;
import com.example.jobhunter.dto.request.auth.LoginRequestDTO;
import com.example.jobhunter.dto.response.user.ResCreateUserDTO;
import com.example.jobhunter.dto.response.user.ResLoginDTO;
import com.example.jobhunter.service.AuthService;
import com.example.jobhunter.service.UserService;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.example.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${hp.jwt.refreshtoken-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("auth/login")
    @ApiMessage("Login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO, @RequestHeader("User-Agent") String userAgent) {
        AuthResponseDTO response = this.authService.login(loginDTO, userAgent);
        // Set cookie
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(refreshTokenExpiration)
                .build();
        ResponseCookie deviceCookie = ResponseCookie.from("device", response.getDeviceKey())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("None")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, deviceCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(response.getResLoginDTO());
    }

    @GetMapping("auth/account")
    @ApiMessage("Get user")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        return ResponseEntity.ok(this.authService.getAccount());
    }

    @GetMapping("auth/refresh")
    @ApiMessage("Get new refresh token")
    public ResponseEntity<ResLoginDTO> getNewRefreshToken(@CookieValue(name = "refresh_token", defaultValue = "none") String refreshToken, @CookieValue(name = "device", defaultValue = "none")String deviceKey){
        AuthResponseDTO response = this.authService.getNewRefreshToken(refreshToken, deviceKey);
        ResponseCookie refreshCookie = ResponseCookie
                .from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        ResponseCookie deviceCookie = ResponseCookie
                .from("device", deviceKey)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenExpiration) // Đồng bộ với refresh token
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, deviceCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(response.getResLoginDTO());
    }

    @PostMapping("auth/logout")
    @ApiMessage("Logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "device", defaultValue = "none") String deviceKey) {
        this.authService.logout(deviceKey);
        //Xóa cookie
        ResponseCookie refreshCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        ResponseCookie deviceCookie = ResponseCookie.from("device", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, deviceCookie.toString());

        return ResponseEntity
                .ok()
                .headers(headers)
                .build();
    }

    @PostMapping("auth/register")
    @ApiMessage("Register a user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(registerRequestDTO));
    }
}
