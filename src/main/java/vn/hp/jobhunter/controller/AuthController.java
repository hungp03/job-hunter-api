package vn.hp.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.entity.User;
import vn.hp.jobhunter.dto.request.ReqLoginDTO;
import vn.hp.jobhunter.dto.response.user.ResCreateUserDTO;
import vn.hp.jobhunter.dto.response.user.ResLoginDTO;
import vn.hp.jobhunter.service.AuthService;
import vn.hp.jobhunter.service.UserService;
import vn.hp.jobhunter.util.SecurityUtil;
import vn.hp.jobhunter.util.annotation.ApiMessage;
import vn.hp.jobhunter.util.error.IdInvalidException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @Value("${hp.jwt.refreshtoken-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("auth/login")
    @ApiMessage("Login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        Map<String, Object> response = this.authService.login(loginDTO);
        // Set cookie
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", (String) response.get("refreshToken"))
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body((ResLoginDTO) response.get("userInfo"));
    }

    @GetMapping("auth/account")
    @ApiMessage("Get user")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        return ResponseEntity.ok(this.authService.getAccount());
    }

    @GetMapping("auth/refresh")
    @ApiMessage("Get new refresh token")
    public ResponseEntity<ResLoginDTO> getNewRefreshToken(@CookieValue(name = "refresh_token", defaultValue = "none") String refreshToken) throws IdInvalidException {
        Map<String, Object> response = this.authService.getNewRefreshToken(refreshToken);
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token",(String) response.get("refreshToken"))
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body((ResLoginDTO) response.get("userInfo"));
    }

    @PostMapping("auth/logout")
    @ApiMessage("Logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        this.authService.logout();
        //Xóa cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);
    }

    @PostMapping("auth/register")
    @ApiMessage("Register a user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user){
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateDTO(newUser));
    }
}
