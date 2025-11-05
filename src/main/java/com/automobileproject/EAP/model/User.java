package com.automobileproject.EAP.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    public enum Role {
        ADMIN, EMPLOYEE, CUSTOMER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10,15}", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    // Email verification fields - ADDED FOR EMAIL VERIFICATION
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    // Token for email verification - ADDED FOR EMAIL VERIFICATION
    private String verificationToken;

    // User enabled status - ADDED FOR SPRING SECURITY COMPATIBILITY
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    // Password reset token - ADDED FOR FORGOT PASSWORD FEATURE
    private String passwordResetToken;

    // Password reset token expiry - ADDED FOR FORGOT PASSWORD FEATURE
    private java.time.LocalDateTime passwordResetTokenExpiry;
}