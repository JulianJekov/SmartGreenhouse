package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.entity.User;

public interface EmailVerificationService {
    void verifyEmail(String token);

    void resendVerificationEmail(String email);

    void sendVerificationEmail(User user);
}
