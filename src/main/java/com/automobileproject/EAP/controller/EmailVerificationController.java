package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.ErrorResponse;
import com.automobileproject.EAP.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {

    private final EmailVerificationService verificationService;

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            log.info("Email verification request received with token: {}", token);

            verificationService.verifyEmail(token);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Email verified successfully! You can now log in.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during email verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Email verification failed: " + e.getMessage()));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        try {
            log.info("Resend verification request for email: {}", email);

            verificationService.resendVerificationEmail(email);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Verification email sent successfully! Please check your inbox.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Resend verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error while resending verification email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to send verification email: " + e.getMessage()));
        }
    }
}