package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.entity.BaseToken;
import com.smartgreenhouse.greenhouse.entity.PasswordResetToken;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.InvalidTokenException;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.PasswordResetTokenRepository;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import com.smartgreenhouse.greenhouse.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenRateLimitService rateLimitService;

    @Value("${app.email.password-reset.expiration-hours}")
    private int expirationHours;

    public PasswordResetServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    EmailService emailService,
                                    TokenRateLimitService rateLimitService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.rateLimitService = rateLimitService;
    }

    @Transactional
    @Override
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        rateLimitService.checkPasswordResetRateLimit(user.getId());
        passwordResetTokenRepository.revokeAllUserTokens(user.getId());

        PasswordResetToken token = generateToken(user);
        passwordResetTokenRepository.save(token);

        emailService.sendPasswordResetEmail(user, token.getToken());
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

    private PasswordResetToken generateToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(expirationHours, ChronoUnit.HOURS));
        return token;
    }

    private void validateToken(BaseToken token) {
        if (token.getUsed() || token.getRevoked()) {
            throw new InvalidTokenException("Token already used");
        }
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token expired");
        }
    }
}

