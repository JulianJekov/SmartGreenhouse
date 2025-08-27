package com.smartgreenhouse.greenhouse.jwt;

import com.smartgreenhouse.greenhouse.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTHelper {

    @Value("${jwt.auth.app}")
    private String appName;

    @Value("${jwt.auth.secret_key}")
    private String secretKey;

    @Value("${jwt.auth.expiration}")
    private int expiresIn;

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .issuer(appName)
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(generateExpirationDate())
                .signWith(getSigninSecretKey())
                .compact();
    }

    public String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public String getUsernameFromToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            throw new JwtException("Failed to extract username from token", e);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            throw new JwtException("Failed to extract role from token", e);
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username != null &&
                username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }

    public boolean validateToken(String token, User user) {
        final String username = getUsernameFromToken(token);
        final String role = getRoleFromToken(token);
        return (username != null &&
                username.equals(user.getEmail()) &&
                role != null &&
                role.equals(user.getRole().name()) &&
                !isTokenExpired(token));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate != null && expirationDate.before(new Date());
    }

    private Date getExpirationDate(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigninSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token", e);
        }
    }

    private SecretKey getSigninSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + expiresIn * 1000L);
    }
}
