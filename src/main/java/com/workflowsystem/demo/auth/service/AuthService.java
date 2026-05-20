package com.workflowsystem.demo.auth.service;

import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.dto.RegisterRequest;
import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register (RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceNotFoundException("Email already exists");
        }
        if(userRepository.existsByUsername(request.getUsername())){
            throw new ResourceNotFoundException("Username already taken");
        }
        
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponse(savedUser);
    }
}
