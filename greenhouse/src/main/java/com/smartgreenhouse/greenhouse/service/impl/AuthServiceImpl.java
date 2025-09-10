package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.user.AuthResponse;
import com.smartgreenhouse.greenhouse.dto.user.LoginRequest;
import com.smartgreenhouse.greenhouse.dto.user.RegisterRequest;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.*;
import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import com.smartgreenhouse.greenhouse.service.AuthService;
import com.smartgreenhouse.greenhouse.service.EmailVerificationService;
import com.smartgreenhouse.greenhouse.util.userMapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTHelper jWTHelper;
    private final EmailVerificationService emailVerificationService;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JWTHelper jWTHelper,
                           EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jWTHelper = jWTHelper;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jWTHelper.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        emailVerificationService.sendVerificationEmail(savedUser);

        AuthResponse authResponse = userMapper.toAuthResponse(savedUser);
        authResponse.setToken(token);

        return authResponse;
    }

    @Transactional
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        if (!user.getEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in. Check your inbox or " +
                    "request a new verification email if needed.");
        }
        String token = jWTHelper.generateToken(user.getEmail(), user.getRole().name());
        AuthResponse authResponse = userMapper.toAuthResponse(user);
        authResponse.setToken(token);
        return authResponse;
    }

}
