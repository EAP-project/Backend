package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.RegistrationRequest;
import com.automobileproject.EAP.mapper.UserMapper;
import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException; // <-- Make sure this is imported
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ADDED FOR EMAIL VERIFICATION
import java.util.UUID;
// ADDED FOR FORGOT PASSWORD FEATURE
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService; // ADDED FOR EMAIL VERIFICATION

    @Transactional
    public User registerUser(RegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        validateUniqueConstraints(request);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate verification token - ADDED FOR EMAIL VERIFICATION
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setEmailVerified(false); // ADDED FOR EMAIL VERIFICATION

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Send verification email - ADDED FOR EMAIL VERIFICATION
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);
            log.info("Verification email sent to: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email, but user was saved", e);
            // Continue registration even if email fails
        }

        return savedUser;
    }

    private void validateUniqueConstraints(RegistrationRequest request) {
        // --- FIX 1: Use .isPresent() instead of != null ---
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // --- FIX 2: Use .isPresent() instead of != null ---
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public User findByUsername(String username) {
        // --- FIX 3: Unwrap the Optional or throw an exception ---
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    public User findByEmail(String email) {
        // --- FIX 4: Unwrap the Optional or throw an exception ---
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    // Verify user's email with token - ADDED FOR EMAIL VERIFICATION
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null); // Clear token after verification
        userRepository.save(user);
        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    // Request password reset - ADDED FOR FORGOT PASSWORD FEATURE
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        // Generate password reset token - ADDED FOR FORGOT PASSWORD FEATURE
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        // Token expires in 1 hour - ADDED FOR FORGOT PASSWORD FEATURE
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));

        userRepository.save(user);
        log.info("Password reset token generated for user: {}", email);

        // Send password reset email - ADDED FOR FORGOT PASSWORD FEATURE
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    // Reset password with token - ADDED FOR FORGOT PASSWORD FEATURE
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        // Check if token has expired - ADDED FOR FORGOT PASSWORD FEATURE
        if (user.getPasswordResetTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        // Update password - ADDED FOR FORGOT PASSWORD FEATURE
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null); // Clear token after use
        user.setPasswordResetTokenExpiry(null); // Clear expiry

        userRepository.save(user);
        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    // Validate password reset token - ADDED FOR FORGOT PASSWORD FEATURE
    public void validatePasswordResetToken(String token) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        // Check if token has expired - ADDED FOR FORGOT PASSWORD FEATURE
        if (user.getPasswordResetTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        log.info("Password reset token validated for user: {}", user.getEmail());
    }
}
