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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public User registerUser(RegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        validateUniqueConstraints(request);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

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
}
