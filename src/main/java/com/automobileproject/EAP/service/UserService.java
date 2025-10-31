package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.RegistrationRequest;
import com.automobileproject.EAP.mapper.UserMapper;
import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.repository.UserRepository;
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
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}