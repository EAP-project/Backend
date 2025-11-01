package com.automobileproject.EAP.mapper;

import com.automobileproject.EAP.dto.LoginResponse;
import com.automobileproject.EAP.dto.RegistrationRequest;
import com.automobileproject.EAP.dto.RegistrationResponse;
import com.automobileproject.EAP.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegistrationRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(request.getRole())
                .phoneNumber(request.getPhoneNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    public RegistrationResponse toRegistrationResponse(User user) {
        return RegistrationResponse.builder()
                .message("User registered successfully")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public LoginResponse toLoginResponse(User user, String token) {
        return LoginResponse.builder()
                .message("Login successful")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .token(token)
                .build();
    }
}