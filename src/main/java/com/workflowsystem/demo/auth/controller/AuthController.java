package com.workflowsystem.demo.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.auth.dto.ForgotPasswordRequest;
import com.workflowsystem.demo.auth.dto.LoginRequest;
import com.workflowsystem.demo.auth.dto.LoginResponse;
import com.workflowsystem.demo.auth.dto.LogoutRequest;
import com.workflowsystem.demo.auth.dto.RefreshTokenRequest;
import com.workflowsystem.demo.auth.dto.RegisterRequest;
import com.workflowsystem.demo.auth.dto.ResetPasswordRequest;
import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.auth.service.AuthService;
import com.workflowsystem.demo.auth.service.CurrentUserService;
import com.workflowsystem.demo.auth.service.PasswordResetService;
import com.workflowsystem.demo.auth.service.RefreshTokenService;
import com.workflowsystem.demo.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/auth")
@Tag(
    name = "Authentication",
    description = "Endpoints for user authentication"
)
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final CurrentUserService currentUserService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, PasswordResetService passwordResetService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetService = passwordResetService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account"
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

    @PostMapping("/logout")
    @Operation(
        summary = "Logout",
        description = "Revokes the refresh token and logs out the user"
    )
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());

        return new ApiResponse<>(
            true,
            "Logout successful",
            null
        );
    }

    @PostMapping("/forgot-password")
    @Operation(
        summary = "Forgot password",
        description = "Generates a password reset token and sends it to the user's email"
    )
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // String token = passwordResetService.generateResetToken(request.getEmail());

        // System.out.println("password reset token email sent: " + token);
        logger.info("Password reset token generated for email: {}", request.getEmail());

        return new ApiResponse<>(
            true,
            "Password reset token sent to email",
            null
        );
    }

    @PostMapping("/reset-password")
    @Operation(
        summary = "Reset password",
        description = "Resets the user's password using the provided reset token"
    )
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);

        return new ApiResponse<>(
            true,
            "Password reset successfully",
            null
        );
    }

    @GetMapping("/me")
    @Operation(
        summary = "Current user",
        description = "Returns the authenticated user's profile"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<UserResponse> me() {
        UserResponse userResponse = UserMapper.toUserResponse(currentUserService.getCurrentUser());

        return new ApiResponse<>(
            true,
            "Current user fetched successfully",
            userResponse
        );
    }
    
}
