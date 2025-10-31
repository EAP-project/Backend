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

            authenticateUser(request.getEmail(), request.getPassword());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String jwt = jwtUtil.generateToken(userDetails);

            User user = userService.findByEmail(request.getEmail());
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
}