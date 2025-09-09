package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.user.AuthResponse;
import com.smartgreenhouse.greenhouse.dto.user.LoginRequest;
import com.smartgreenhouse.greenhouse.dto.user.RegisterRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.entity.BaseToken;
import com.smartgreenhouse.greenhouse.entity.EmailVerificationToken;
import com.smartgreenhouse.greenhouse.entity.PasswordResetToken;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.*;
import com.smartgreenhouse.greenhouse.jwt.JWTHelper;
import com.smartgreenhouse.greenhouse.repository.EmailVerificationTokenRepository;
import com.smartgreenhouse.greenhouse.repository.PasswordResetTokenRepository;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import com.smartgreenhouse.greenhouse.service.UserService;
import com.smartgreenhouse.greenhouse.util.userMapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class UserServiceImpl implements UserService {

    @Value("${app.email.verification.expiration-hours}")
    private int verificationExpiryHours;

    @Value("${app.email.password-reset.expiration-hours}")
    private int passwordResetExpiryHours;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTHelper jWTHelper;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JWTHelper jWTHelper,
                           EmailVerificationTokenRepository tokenRepository,
                           EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jWTHelper = jWTHelper;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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

        AuthResponse authResponse = userMapper.toAuthResponse(savedUser);
        authResponse.setToken(token);

        EmailVerificationToken emailVerificationToken = generateToken
                (user, EmailVerificationToken::new, verificationExpiryHours);

        tokenRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(user, emailVerificationToken.getToken());

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


    @Transactional
    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        validateToken(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }

    @Transactional
    @Override
    public void resendVerificationEmail(String email) {
        User user = getUserByEmail(email);

        tokenRepository.revokeAllUserTokens(user.getId());

        EmailVerificationToken emailVerificationToken = generateToken
                (user, EmailVerificationToken::new, verificationExpiryHours);
        tokenRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(user, emailVerificationToken.getToken());
    }

    @Transactional
    @Override
    public void createPasswordResetToken(String email) {
        User user = getUserByEmail(email);
        LOGGER.info("Creating password reset token for email: {}", email);

        passwordResetTokenRepository.revokeAllUserTokens(user.getId());

        PasswordResetToken passwordResetToken = generateToken
                (user, PasswordResetToken::new, passwordResetExpiryHours);

        passwordResetTokenRepository.save(passwordResetToken);
        LOGGER.info("Sending password reset email to: {}", email);
        emailService.sendPasswordResetEmail(user, passwordResetToken.getToken());
        LOGGER.info("Password reset email sent to: {}", email);
    }

    @Transactional
    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        validateToken(passwordResetToken);

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Transactional
    @Override
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    private <T extends BaseToken> T generateToken(User user, Supplier<T> tokenConstructor, int expiryHours) {
        T token = tokenConstructor.get();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(expiryHours, ChronoUnit.HOURS));
        return token;
    }

    private static void validateToken(BaseToken token) {
        if (token.getUsed() || token.getRevoked()) {
            throw new InvalidTokenException("Token already used");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token expired");
        }
    }
}
