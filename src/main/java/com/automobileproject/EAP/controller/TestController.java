package com.automobileproject.EAP.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/hello")
    public String hello(Authentication authentication) {
        return "Hello, " + authentication.getName() + "! Your JWT is working correctly.";
    }

    @GetMapping("/admin")
    public String adminOnly() {
        return "This is an admin-only endpoint";
    }
}