package com.workflowsystem.demo.shared.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureTestController {

    @GetMapping("/secure")
    public String secureEndpoint(Authentication authentication) {

        return "Auth User:" + authentication.getName();
    }
}