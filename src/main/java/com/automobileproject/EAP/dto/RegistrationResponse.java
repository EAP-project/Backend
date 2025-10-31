package com.automobileproject.EAP.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {
    private String message;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
}