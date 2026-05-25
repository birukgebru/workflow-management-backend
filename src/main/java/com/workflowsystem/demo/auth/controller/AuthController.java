package com.workflowsystem.demo.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.auth.dto.LoginRequest;
import com.workflowsystem.demo.auth.dto.LoginResponse;
import com.workflowsystem.demo.auth.dto.RefreshTokenRequest;
import com.workflowsystem.demo.auth.dto.RegisterRequest;
import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.service.AuthService;
import com.workflowsystem.demo.shared.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/auth")
@Tag(
    name = "Authentication",
    description = "Endpoints for user authentication"
)
@ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "User registered successfully"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Validation failed"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "Access denied"
    )
})
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account"
    )
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = authService.register(request);

        return new ApiResponse<>(
            true,
            "User registered successfully",
            userResponse
        );
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticates a user and returns a login response"
    )
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        
        return new ApiResponse<>(
            true,
            "Login successful",
            loginResponse
        );
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh token",
        description = "Refreshes the authentication token"
    )
    public ApiResponse<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        LoginResponse loginResponse = authService.refreshToken(request);

        return new ApiResponse<>(
            true,
            "Token refreshed successfully",
            loginResponse
        );
    }
}
