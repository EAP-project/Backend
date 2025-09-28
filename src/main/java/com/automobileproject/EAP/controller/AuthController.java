package com.automobileproject.EAP.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            System.out.println("Received registration request for: " + user.getUsername() + " with role: " + user.getRole());

            // Validate role
            if (user.getRole() == null) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Role is required. Available roles: MANAGER, CUSTOMER, SUPERVISOR, TECHNICIAN")
                );
            }

            User savedUser = userService.registerUser(user);
            System.out.println("User registered successfully: " + savedUser.getUsername());

            return ResponseEntity.ok(
                    new RegistrationResponse("User registered successfully",
                            savedUser.getUsername(),
                            savedUser.getEmail(),
                            savedUser.getRole().name(),
                            savedUser.getFirstName(),
                            savedUser.getLastName())
            );
        } catch (RuntimeException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Registration failed: " + e.getMessage())
            );
        } catch (Exception e) {
            System.out.println("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse("Internal server error: " + e.getMessage())
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Received login request for: " + loginRequest.getUsername());

            // Basic validation
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty() ||
                    loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Username and password are required")
                );
            }

            // Authenticate user
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ErrorResponse("Invalid username or password")
                );
            }

            // Load user details and generate token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            // Find user for additional details
            User user = userService.findByUsername(loginRequest.getUsername());

            System.out.println("Login successful for: " + user.getUsername() + " with role: " + user.getRole());
            return ResponseEntity.ok(new LoginResponse(
                    "Login successful",
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    jwt
            ));

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse("Login error: " + e.getMessage())
            );
        }
    }

    // Request DTO for Login
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // Response DTOs
    public static class RegistrationResponse {
        private String message;
        private String username;
        private String email;
        private String role;
        private String firstName;
        private String lastName;

        public RegistrationResponse(String message, String username, String email, String role, String firstName, String lastName) {
            this.message = message;
            this.username = username;
            this.email = email;
            this.role = role;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    public static class LoginResponse {
        private String message;
        private String username;
        private String email;
        private String role;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String token;
        private String tokenType = "Bearer";

        public LoginResponse(String message, String username, String email, String role, String firstName, String lastName, String phoneNumber, String token) {
            this.message = message;
            this.username = username;
            this.email = email;
            this.role = role;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.token = token;
        }

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}