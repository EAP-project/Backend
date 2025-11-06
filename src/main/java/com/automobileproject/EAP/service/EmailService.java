// Email service to send verification emails - NEW FILE FOR EMAIL VERIFICATION
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

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // Send verification email to user - ADDED FOR EMAIL VERIFICATION
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Email Verification - Automobile Service");

            // Construct verification link - ADDED FOR EMAIL VERIFICATION
            String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;

            message.setText("Dear User,\n\n" +
                    "Thank you for registering with our Automobile Service.\n\n" +
                    "Please click the link below to verify your email address:\n" +
                    verificationLink + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you did not create an account, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Automobile Service Team");

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    // Send password reset email - ADDED FOR FORGOT PASSWORD FEATURE
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request - Automobile Service");

            // Construct password reset link - ADDED FOR FORGOT PASSWORD FEATURE
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            message.setText("Dear User,\n\n" +
                    "We received a request to reset your password for your Automobile Service account.\n\n" +
                    "Please click the link below to reset your password:\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you did not request a password reset, please ignore this email. Your password will remain unchanged.\n\n"
                    +
                    "Best regards,\n" +
                    "Automobile Service Team");

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }
}
