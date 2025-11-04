package com.automobileproject.EAP.service;

import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.model.VerificationToken;
import com.automobileproject.EAP.repository.UserRepository;
import com.automobileproject.EAP.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public void createVerificationToken(User user) {
        // Delete any existing tokens for this user
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .createdDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);
        log.info("Verification token created for user: {}", user.getEmail());

        // Send verification email
        emailService.sendVerificationEmail(
                user.getEmail(),
                token,
                user.getFirstName()
        );
    }

    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            log.warn("Verification token expired for token: {}", token);
            throw new IllegalArgumentException("Verification token has expired");
        }

        if (verificationToken.getVerifiedDate() != null) {
            log.warn("Token already used: {}", token);
            throw new IllegalArgumentException("Email already verified");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEnabled(true);
        userRepository.save(user);

        verificationToken.setVerifiedDate(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        log.info("Email verified successfully for user: {}", user.getEmail());
        return true;
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }

        createVerificationToken(user);
        log.info("Verification email resent to: {}", email);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Expired verification tokens cleaned up");
    }
}