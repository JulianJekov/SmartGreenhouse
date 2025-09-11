package com.smartgreenhouse.greenhouse.config;

import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.jwt.JwtAuthFilter;
import com.smartgreenhouse.greenhouse.oauth.CustomOAuth2UserService;
import com.smartgreenhouse.greenhouse.oauth.OAuth2AuthenticationFailureHandler;
import com.smartgreenhouse.greenhouse.oauth.OAuth2AuthenticationSuccessHandler;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import com.smartgreenhouse.greenhouse.service.RefreshTokenService;
import com.smartgreenhouse.greenhouse.util.CookieUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JWTHelper jwtHelper;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;

    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthFilter jwtAuthFilter,
                          JWTHelper jwtHelper,
                          RefreshTokenService refreshTokenService, UserRepository userRepository, CookieUtil cookieUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtHelper = jwtHelper;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.cookieUtil = cookieUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/verify/**", "/api/password/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService())
                        )
                        .successHandler(oauth2SuccessHandler())
                        .failureHandler(oath2FailureHandler())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(userRepository, passwordEncoder());
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oauth2SuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtHelper, refreshTokenService, cookieUtil);
    }

    @Bean
    public OAuth2AuthenticationFailureHandler oath2FailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
