package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.user.*;
import com.smartgreenhouse.greenhouse.entity.RefreshToken;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.InvalidTokenException;
import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.service.RefreshTokenService;
import com.smartgreenhouse.greenhouse.service.AuthService;
import com.smartgreenhouse.greenhouse.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication, JWT and refresh token handling")
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

    @Operation(summary = "Register a new user", description = "Creates a new user account and sends verification email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login user", description = "Authenticates user and issues JWT + refresh token cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Email not verified"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest,
                                                  HttpServletResponse response) {

        AuthResponse authResponse = authService.login(loginRequest);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(loginRequest.getEmail());

        cookieUtil.setRefreshTokenCookie(response, refreshToken.getToken());

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Logout user", description = "Revokes refresh token and clear cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
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

    @Operation(
            summary = "Refresh JWT token",
            description = "Generates new access and refresh tokens using the refresh token cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New token issued",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
