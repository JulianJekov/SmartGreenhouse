package com.smartgreenhouse.greenhouse.oauth;

import com.smartgreenhouse.greenhouse.entity.RefreshToken;
import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.service.RefreshTokenService;
import com.smartgreenhouse.greenhouse.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTHelper jwtHelper;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;

    public OAuth2AuthenticationSuccessHandler(JWTHelper jwtHelper,
                                              RefreshTokenService refreshTokenService, CookieUtil cookieUtil) {
        this.jwtHelper = jwtHelper;
        this.refreshTokenService = refreshTokenService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        String accessToken = jwtHelper.generateToken(email, "USER");
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(email);

        cookieUtil.setRefreshTokenCookie(response,refreshToken.getToken());

        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"token\": \"%s\", \"email\": \"%s\"}",
                        accessToken, email)
        );

        clearAuthenticationAttributes(request);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

