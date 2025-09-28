package com.example.demo.service.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // Validate input
        if (user == null || !StringUtils.hasText(user.getUsername()) ||
                !StringUtils.hasText(user.getPassword()) || !StringUtils.hasText(user.getEmail()) ||
                user.getRole() == null || !StringUtils.hasText(user.getPhoneNumber()) ||
                !StringUtils.hasText(user.getFirstName()) || !StringUtils.hasText(user.getLastName())) {
            throw new RuntimeException("All fields are required: username, password, email, role, phoneNumber, firstName, lastName");
        }

        // Check if username already exists
        User existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername != null) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        User existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail != null) {
            throw new RuntimeException("Email already exists");
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}