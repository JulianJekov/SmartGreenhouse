package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.user.*;
import com.smartgreenhouse.greenhouse.entity.RefreshToken;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.InvalidTokenException;
import com.smartgreenhouse.greenhouse.exceptions.TokenException;
import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.service.RefreshTokenService;
import com.smartgreenhouse.greenhouse.service.UserService;
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
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JWTHelper jWTHelper;

    public UserController(UserService userService, RefreshTokenService refreshTokenService, JWTHelper jWTHelper) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jWTHelper = jWTHelper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam String email) {
        userService.resendVerificationEmail(email);
        return ResponseEntity.ok("Verification email sent");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok("If the email exists, a reset link has been sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest,
                                                  HttpServletResponse response) {

        AuthResponse authResponse = userService.login(loginRequest);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(loginRequest.getEmail());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/user/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/user/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

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

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/user/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(newAccessToken);
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole());

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO currentUser = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(currentUser);
    }

}
