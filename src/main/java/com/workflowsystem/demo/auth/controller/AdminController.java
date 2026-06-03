package com.workflowsystem.demo.auth.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.workflowsystem.demo.auth.dto.AssignRoleRequest;
import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.auth.service.UserService;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;



@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") 
@SecurityRequirement(name = "bearerAuth")
@Tag(
    name = "Admin Management",
    description = "Endpoints for managing admin-specific operations"
)
public class AdminController {
    private final UserService userService;
    private final UserRepository userRepository;

    public AdminController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PutMapping("/users/{id}/roles")
    public ApiResponse<UserResponse> assignRole(@PathVariable Long id, @Valid @RequestBody AssignRoleRequest request, Authentication authentication) {

        UserResponse user = userService.assignRole(id, request.getRole(), getCurrentUser(authentication));




        return new ApiResponse<>(
                true,
                "Role assigned successfully",
                user
        );
    }


        // Helpers 
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
