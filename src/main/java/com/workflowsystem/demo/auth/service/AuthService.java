package com.workflowsystem.demo.auth.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.dto.LoginRequest;
import com.workflowsystem.demo.auth.dto.LoginResponse;
import com.workflowsystem.demo.auth.dto.RefreshTokenRequest;
import com.workflowsystem.demo.auth.dto.RegisterRequest;
import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.entity.RefreshToken;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.auth.security.JwtService;
import com.workflowsystem.demo.shared.exception.AuthenticationException;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );
        user.getRoles().add(Role.ROLE_USER);

        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow( () -> new AuthenticationException("Invalid email or password")); 

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!passwordMatches){
            throw new AuthenticationException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                user.getId(), 
                user.getUsername(), 
                user.getEmail(), 
                accessToken, 
                refreshToken.getToken()
            );

    }

    public LoginResponse refreshToken(RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                                    .orElseThrow(()->
                                        new AuthenticationException("Invalid Refresh token")
                                    );

        if(refreshTokenService.isExpired(refreshToken)){
            throw new AuthenticationException("Refresh toke expired");
        }

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            newAccessToken,
            refreshToken.getToken()
        );
    }



}
