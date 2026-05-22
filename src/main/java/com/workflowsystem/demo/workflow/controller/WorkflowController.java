package com.workflowsystem.demo.workflow.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.service.WorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {
    private final WorkflowService workflowService;
    private final UserRepository userRepository;

    public WorkflowController(WorkflowService workflowService, UserRepository userRepository) {
        this.workflowService = workflowService;
        this.userRepository = userRepository;
    }

    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<WorkflowResponse> submitRequest(
            @Valid @RequestBody WorkflowSubmitRequest request,
            Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        WorkflowResponse workflowResponse = workflowService.submitRequest(request, currentUser);

        return new ApiResponse<>(
            true,
            "Workflow request submitted successfully",
            workflowResponse
        );
    }
}
