package com.workflowsystem.demo.workflow.dto;

import jakarta.validation.constraints.NotBlank;

public class AssignApproverRequest {
    @NotBlank
    private Long approverId;
}
