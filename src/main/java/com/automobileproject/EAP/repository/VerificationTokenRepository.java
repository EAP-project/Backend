package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.VerificationToken;
import com.automobileproject.EAP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
    void deleteByExpiryDateBefore(LocalDateTime now);
}