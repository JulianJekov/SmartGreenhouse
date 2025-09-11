package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.user.*;
import com.smartgreenhouse.greenhouse.entity.RefreshToken;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.InvalidTokenException;
import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.service.RefreshTokenService;
import com.smartgreenhouse.greenhouse.service.AuthService;
import com.smartgreenhouse.greenhouse.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JWTHelper jWTHelper;
    private final CookieUtil cookieUtil;

    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService,
                          JWTHelper jWTHelper, CookieUtil cookieUtil) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jWTHelper = jWTHelper;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest,
                                                  HttpServletResponse response) {

        AuthResponse authResponse = authService.login(loginRequest);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(loginRequest.getEmail());

        cookieUtil.setRefreshTokenCookie(response, refreshToken.getToken());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        cookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            throw new InvalidTokenException("Refresh token not found");
        }

        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(refreshToken);
        User user = verifiedToken.getUser();
        String newAccessToken = jWTHelper.generateToken(user.getEmail(), user.getRole().name());
        RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(user.getEmail());

        cookieUtil.setRefreshTokenCookie(response, newRefreshToken.getToken());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(newAccessToken);
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole());

        return ResponseEntity.ok(authResponse);
    }
}
