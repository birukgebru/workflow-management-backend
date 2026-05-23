package com.workflowsystem.demo.workflow.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowHistoryResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;
import com.workflowsystem.demo.workflow.repository.WorkflowHistoryRepository;
import com.workflowsystem.demo.workflow.service.WorkflowService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/workflow")
public class WorkflowController {
    private final WorkflowService workflowService;
    private final UserRepository userRepository;
    private final WorkflowHistoryRepository workflowHistoryRepository;

    public WorkflowController(WorkflowService workflowService, UserRepository userRepository, WorkflowHistoryRepository workflowHistoryRepository) {
        this.workflowService = workflowService;
        this.userRepository = userRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
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

    @GetMapping("/my-requests")
    @PreAuthorize("hasAnyRole('User', 'ADMIN')")
    public ApiResponse<List<WorkflowResponse>> getMyRequests(Authentication authentication){
        User currentUser = userRepository.findByEmail(authentication.getName())
                            .orElseThrow(()-> new ResourceNotFoundException("Authenticated user not found"));

        List<WorkflowResponse> workflowResponses = workflowService.getMyRequests(currentUser);

        return new ApiResponse<>(
            true,
            "List of request",
            workflowResponses
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<List<WorkflowResponse>> getRequestByStatus(@PathVariable WorkflowStatus status) {
        List<WorkflowResponse> workflowResponses = workflowService.getRequestsByStatus(status);
        return new ApiResponse<>(
            true,
            "requests by status",
            workflowResponses
        );
    }
    
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<List<WorkflowHistoryResponse>> getWorkflowHistory(@PathVariable Long id) {
        List<WorkflowHistoryResponse> workflowHistoryResponses = workflowHistoryRepository
                .findByWorkflowRequestIdOrderByChangedAtAsc(id)
                .stream()
                .map(history -> new WorkflowHistoryResponse(
                        history.getAction(),
                        history.getPreviousStatus(),
                        history.getNewStatus(),
                        history.getChangedBy() == null ? null : UserMapper.toUserResponse(history.getChangedBy()),
                        history.getChangedAt()
                ))
                .toList();

        return new ApiResponse<>(
            true,
            "Workflow history",
            workflowHistoryResponses
        );
    }
    
}
