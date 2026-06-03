package com.workflowsystem.demo.auth.dto;

import com.workflowsystem.demo.auth.enums.Role;

import jakarta.validation.constraints.NotNull;

public class AssignRoleRequest {

    @NotNull
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
