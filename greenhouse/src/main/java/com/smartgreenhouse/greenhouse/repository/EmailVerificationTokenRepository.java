package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);

    @Modifying
    @Query("UPDATE EmailVerificationToken t SET t.revoked = true WHERE t.user.id = :userId")
    void revokeAllUserTokens(@Param("userId") Long id);
}
