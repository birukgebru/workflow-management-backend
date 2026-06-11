package com.workflowsystem.demo.auth.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.workflowsystem.demo.auth.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.roleRepository = roleRepository;
    }

    public UserResponse register(RegisterRequest request) {
        logger.info("Registration attempt for email: {}", request.getEmail());
        if(userRepository.existsByEmail(request.getEmail())){
            logger.warn("Registration failed. Email already exists: {}", request.getEmail());
            throw new ResourceNotFoundException("Email already exists");
        }
        if(userRepository.existsByUsername(request.getUsername())){
            logger.warn("Registration failed. Username already taken: {}", request.getUsername());
            throw new ResourceNotFoundException("Username already taken");
        }
        logger.info("Registration details for email: {}", request.getEmail());
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );
        com.workflowsystem.demo.auth.entity.Role defaultRole = roleRepository.findByName(Role.ROLE_REQUESTER)
            .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow( () -> new AuthenticationException("Invalid email or password")); 

        if (!user.isEnabled()) {
            logger.warn("Login failed. Account is disabled: {}", request.getEmail());
            throw new AuthenticationException("Account is disabled");
        }

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!passwordMatches){
            logger.warn("Login failed. Invalid credentials for email: {}", request.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(user);
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
        logger.info("Refresh token request received");

        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                                    .orElseThrow(()->
                                        new AuthenticationException("Invalid Refresh token")
                                    );

        if(refreshTokenService.isExpired(refreshToken)){
            throw new AuthenticationException("Refresh token expired");
        }

        if(refreshToken.isRevoked()){
            throw new AuthenticationException("Refresh token revoked");
        }
        logger.info("Refresh token is valid for user: {}", refreshToken.getUser().getEmail());

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);

        return new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            newAccessToken,
            refreshToken.getToken()
        );
    }



}
