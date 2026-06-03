package com.workflowsystem.demo.auth.controller;

import org.springframework.web.bind.annotation.RestController;
import com.workflowsystem.demo.auth.dto.AssignRoleRequest;
import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.auth.service.UserService;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Operation(
    summary = "Assign Role",
    description = "Assigns a role to a user"
    )
    public ApiResponse<UserResponse> assignRole(
        @PathVariable Long id, 
        @Valid @RequestBody AssignRoleRequest request, 
        Authentication authentication
    ) {
        UserResponse user = userService.assignRole(id, request.getRole(), getCurrentUser(authentication));

        return new ApiResponse<>(
                true,
                "Role assigned successfully",
                user
        );
    }

    @GetMapping("/users")
    @Operation(
        summary = "Get All Users",
        description = "Retrieves a list of all users"
    )
    public ApiResponse<List<UserResponse>> getAllUsers() {

        return new ApiResponse<>(
                true,
                "All users retrieved",
                userService.getAllUsers()
        );
    }

    @GetMapping("/users/{id}")
    @Operation(
        summary = "Get User by ID",
        description = "Retrieves a user by their ID"
    )
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {

        return new ApiResponse<>(
                true,
                "User retrieved",
                userService.getUserById(id)
        );
    }

    @PutMapping("/users/{id}/disable")
    @Operation(
        summary = "Disable User",
        description = "Disables a user account"
    )
    public ApiResponse<UserResponse> disableUser(@PathVariable Long id, Authentication authentication) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User currentUser = getCurrentUser(authentication);
        if (currentUser.getId().equals(id)) {
            throw new IllegalStateException("You cannot disable your own account");
        }

        long adminCount = userRepository.countByRoles_Name(Role.ROLE_ADMIN);
        boolean userIsAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.ROLE_ADMIN);
        if (userIsAdmin && adminCount <= 1) {
            throw new IllegalStateException("Cannot disable last admin");
        }
 

        return new ApiResponse<>(
                true,
                "User disabled successfully",
                userService.disableUser(id, currentUser)
        );
    }

    @PutMapping("/users/{id}/enable")
    @Operation(
        summary = "Enable User",
        description = "Enables a user account"
    )
    public ApiResponse<UserResponse> enableUser(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return new ApiResponse<>(
                true,
                "User enabled successfully",
                userService.enableUser(id, currentUser)
        );
    }

    // Helpers 
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
