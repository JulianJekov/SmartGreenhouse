package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.user.AuthResponse;
import com.smartgreenhouse.greenhouse.dto.user.LoginRequest;
import com.smartgreenhouse.greenhouse.dto.user.RegisterRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.entity.User;

public interface UserService {
    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

    UserDTO getCurrentUser(String email);

    User getUserByEmail(String userEmail);

    void verifyEmail(String token);

    void resendVerificationEmail(String email);

    void createPasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}
