package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.*;
import com.automobileproject.EAP.mapper.UserMapper;
import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.service.UserService;
import com.automobileproject.EAP.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// ADDED FOR EMAIL VERIFICATION
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
        try {
            log.info("Registration request received for: {}", request.getEmail());

            User savedUser = userService.registerUser(request);
            RegistrationResponse response = userMapper.toRegistrationResponse(savedUser);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Login request received for: {}", request.getEmail());

            // Check if user exists and email is verified - ADDED FOR EMAIL VERIFICATION
            User user = userService.findByEmail(request.getEmail());
            if (!user.getEmailVerified()) {
                log.warn("Login attempt with unverified email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Please verify your email address before logging in. Check your inbox for the verification link."));
            }

            authenticateUser(request.getEmail(), request.getPassword());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String jwt = jwtUtil.generateToken(userDetails);

            LoginResponse response = userMapper.toLoginResponse(user, jwt);

            log.info("Login successful for: {}", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid email or password"));

        } catch (Exception e) {
            log.error("Login error for: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login error: " + e.getMessage()));
        }
    }

    private void authenticateUser(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    // Email verification endpoint - ADDED FOR EMAIL VERIFICATION
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            log.info("Email verification request received with token");

            userService.verifyEmail(token);

            return ResponseEntity.ok("Email verified successfully! You can now log in to your account.");

        } catch (IllegalArgumentException e) {
            log.warn("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Error during email verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Verification error: " + e.getMessage()));
        }
    }

    // Forgot password endpoint - ADDED FOR FORGOT PASSWORD FEATURE
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            log.info("Forgot password request received for: {}", request.getEmail());

            userService.requestPasswordReset(request.getEmail());

            return ResponseEntity.ok("Password reset email sent successfully. Please check your inbox.");

        } catch (Exception e) {
            log.error("Forgot password error for: {}", request.getEmail(), e);
            // Don't reveal if email exists or not for security - ADDED FOR FORGOT PASSWORD FEATURE
            return ResponseEntity.ok("If the email exists, a password reset link will be sent.");
        }
    }

    // Reset password endpoint - ADDED FOR FORGOT PASSWORD FEATURE
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            log.info("Reset password request received");

            userService.resetPassword(request.getToken(), request.getNewPassword());

            return ResponseEntity.ok("Password reset successfully! You can now log in with your new password.");

        } catch (IllegalArgumentException e) {
            log.warn("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Error during password reset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Password reset error: " + e.getMessage()));
        }
    }

    // Validate password reset token from email link - ADDED FOR FORGOT PASSWORD FEATURE
    @GetMapping("/reset-password")
    public ResponseEntity<?> validateResetToken(@RequestParam("token") String token) {
        try {
            log.info("Password reset token validation request received");

            // Validate the token exists and is not expired
            userService.validatePasswordResetToken(token);

            return ResponseEntity.ok("Token is valid. Please use POST /api/reset-password with your new password. Token: " + token);

        } catch (IllegalArgumentException e) {
            log.warn("Password reset token validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Error validating password reset token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Token validation error: " + e.getMessage()));
        }
    }
}