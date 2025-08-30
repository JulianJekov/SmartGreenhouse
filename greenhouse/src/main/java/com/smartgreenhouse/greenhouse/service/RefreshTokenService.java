package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(String email);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(String token);
}
