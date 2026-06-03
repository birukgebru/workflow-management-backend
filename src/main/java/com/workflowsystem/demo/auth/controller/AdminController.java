package com.workflowsystem.demo.auth.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.workflowsystem.demo.auth.dto.AssignRoleRequest;
import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.service.UserService;
import com.workflowsystem.demo.shared.response.ApiResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



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


    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users/{id}/roles")
    public ApiResponse<UserResponse> assignRole(@PathVariable Long id, @Valid @RequestBody AssignRoleRequest request) {

        UserResponse user = userService.assignRole(id, request.getRole());

        return new ApiResponse<>(
                true,
                "Role assigned successfully",
                user
        );
    }
}
