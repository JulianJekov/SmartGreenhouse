package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.revoked = true WHERE t.user.id = :userId AND t.revoked = false ")
    void revokeAllUserTokens(@Param("userId") Long id);

    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.user.id = :userId AND t.created > :oneHourAgo")
    long countRecentTokensByUserId(@Param("userId") Long userId, @Param("oneHourAgo") Instant oneHourAgo);
}
