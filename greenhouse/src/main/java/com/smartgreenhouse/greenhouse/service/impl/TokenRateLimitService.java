package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.exceptions.TooManyRequestsException;
import com.smartgreenhouse.greenhouse.repository.EmailVerificationTokenRepository;
import com.smartgreenhouse.greenhouse.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenRateLimitService {

    @Value("${app.email.verification.max-attempts-per-hour}")
    private int verificationMaxAttempts;

    @Value("${app.email.password-reset.max-attempts-per-hour}")
    private int passwordResetMaxAttempts;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public TokenRateLimitService(EmailVerificationTokenRepository emailVerificationTokenRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public void checkVerificationRateLimit(Long userId) {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        long count = emailVerificationTokenRepository.countRecentTokensByUserId(userId, oneHourAgo);
        if (count >= verificationMaxAttempts) {
            throw new TooManyRequestsException("Too many verification requests. Please try again later.");
        }
    }

    public void checkPasswordResetRateLimit(Long userId) {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        long count = passwordResetTokenRepository.countRecentTokensByUserId(userId, oneHourAgo);
        if (count >= passwordResetMaxAttempts) {
            throw new TooManyRequestsException("Too many verification requests. Please try again later.");
        }
    }
}
