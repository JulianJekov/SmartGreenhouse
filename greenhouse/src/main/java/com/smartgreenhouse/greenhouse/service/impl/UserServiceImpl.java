package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.user.AuthResponse;
import com.smartgreenhouse.greenhouse.dto.user.LoginRequest;
import com.smartgreenhouse.greenhouse.dto.user.RegisterRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.entity.EmailVerificationToken;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.*;
import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.repository.EmailVerificationTokenRepository;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import com.smartgreenhouse.greenhouse.service.UserService;
import com.smartgreenhouse.greenhouse.util.userMapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Value("${app.email.verification.expiration-hours}")
    private static Integer verificationExpiryHours;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTHelper jWTHelper;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JWTHelper jWTHelper,
                           EmailVerificationTokenRepository tokenRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jWTHelper = jWTHelper;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jWTHelper.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        AuthResponse authResponse = userMapper.toAuthResponse(savedUser);
        authResponse.setToken(token);

        EmailVerificationToken emailVerificationToken = generateVerificationToken(user);

        tokenRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(user, emailVerificationToken.getToken());

        return authResponse;
    }


    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (verificationToken.getUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = getUserByEmail(email);

        EmailVerificationToken emailVerificationToken = generateVerificationToken(user);
        tokenRepository.save(emailVerificationToken);
        emailService.sendVerificationEmail(user, emailVerificationToken.getToken());
    }

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

    @Override
    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    @Override
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    private static EmailVerificationToken generateVerificationToken(User user) {
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setUser(user);
        emailVerificationToken.setToken(UUID.randomUUID().toString());
        emailVerificationToken.setExpiryDate(Instant.now().plus(verificationExpiryHours, ChronoUnit.HOURS));
        return emailVerificationToken;
    }
}
