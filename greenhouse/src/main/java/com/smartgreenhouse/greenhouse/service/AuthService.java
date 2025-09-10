package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.user.AuthResponse;
import com.smartgreenhouse.greenhouse.dto.user.LoginRequest;
import com.smartgreenhouse.greenhouse.dto.user.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);
}
