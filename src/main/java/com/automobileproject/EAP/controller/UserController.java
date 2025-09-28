package com.automobileproject.EAP.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/")
    public String home() {
        return "Hello World! Spring Boot is working!";
    }
    @GetMapping("/test")
    public String test() {
        return "Test endpoint is working!";
    }
}
