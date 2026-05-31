package com.workflowsystem.demo.auth.controller;

import org.springframework.web.bind.annotation.RestController;
import com.workflowsystem.demo.auth.dto.ForgotPasswordRequest;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.auth.service.PasswordResetService;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;

import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/password-reset")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    
    public PasswordResetController(PasswordResetService passwordResetService, UserRepository userRepository) {
        this.passwordResetService = passwordResetService;
        this.userRepository = userRepository;
    }


}
