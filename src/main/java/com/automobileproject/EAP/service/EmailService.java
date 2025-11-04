package com.automobileproject.EAP.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@example.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, String token, String userName) {
        try {
            String verificationUrl = baseUrl + "/api/verify-email?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Email Verification - Automobile Garage");
            message.setText(buildEmailContent(userName, verificationUrl));

            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildEmailContent(String userName, String verificationUrl) {
        return String.format("""
            Hello %s,
            
            Thank you for registering with Automobile Garage!
            
            Please click the link below to verify your email address:
            %s
            
            This link will expire in 24 hours.
            
            If you didn't create an account, please ignore this email.
            
            Best regards,
            Automobile Garage Team
            """, userName, verificationUrl);
    }

    public void sendPasswordResetEmail(String toEmail, String token, String userName) {
        try {
            String resetUrl = baseUrl + "/api/reset-password?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request - Automobile Garage");
            message.setText(buildPasswordResetContent(userName, resetUrl));

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetContent(String userName, String resetUrl) {
        return String.format("""
            Hello %s,
            
            We received a request to reset your password.
            
            Please click the link below to reset your password:
            %s
            
            This link will expire in 1 hour.
            
            If you didn't request a password reset, please ignore this email.
            
            Best regards,
            Automobile Garage Team
            """, userName, resetUrl);
    }
}