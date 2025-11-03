package com.automobileproject.EAP.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerOnly(Authentication authentication) {
        return "Hello, " + authentication.getName() + "! This is a customer-only endpoint.";
    }

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String employeeOnly(Authentication authentication) {
        return "Hello, " + authentication.getName() + "! This is an employee-only endpoint.";
    }
}