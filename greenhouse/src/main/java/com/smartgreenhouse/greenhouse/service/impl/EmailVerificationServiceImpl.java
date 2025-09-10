package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.entity.EmailVerificationToken;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.InvalidTokenException;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.EmailVerificationTokenRepository;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import com.smartgreenhouse.greenhouse.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Value("${app.email.verification.expiration-hours}")
    private int expirationHours;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final TokenRateLimitService tokenRateLimitService;

    public EmailVerificationServiceImpl(EmailVerificationTokenRepository emailVerificationTokenRepository,
                                        EmailService emailService,
                                        UserRepository userRepository,
                                        TokenRateLimitService tokenRateLimitService) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.tokenRateLimitService = tokenRateLimitService;
    }

    @Transactional
    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        validateToken(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationToken.setUsed(true);
        emailVerificationTokenRepository.save(verificationToken);
    }

    @Transactional
    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        tokenRateLimitService.checkVerificationRateLimit(user.getId());

        emailVerificationTokenRepository.revokeAllUserTokens(user.getId());

        EmailVerificationToken emailVerificationToken = generateToken(user);
        emailVerificationTokenRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(user, emailVerificationToken.getToken());
    }

    @Transactional
    @Override
    public void sendVerificationEmail(User user) {
        EmailVerificationToken token = generateToken(user);
        emailVerificationTokenRepository.save(token);
        emailService.sendVerificationEmail(user, token.getToken());
    }

    private EmailVerificationToken generateToken(User user) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(expirationHours, ChronoUnit.HOURS));
        return token;
    }

    private void validateToken(EmailVerificationToken verificationToken) {
        if (verificationToken.getUsed() || verificationToken.getRevoked()) {
            throw new InvalidTokenException("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token expired");
        }
    }
}
