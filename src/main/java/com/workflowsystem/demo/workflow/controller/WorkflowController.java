package com.workflowsystem.demo.workflow.controller;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowHistoryResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;
import com.workflowsystem.demo.workflow.mapper.WorkflowHistoryMapper;
import com.workflowsystem.demo.workflow.repository.WorkflowHistoryRepository;
import com.workflowsystem.demo.workflow.service.WorkflowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/workflow")
@SecurityRequirement(name = "bearerAuth")
@Tag(
    name = "Workflow Management",
    description = "Endpoints for managing workflow requests"
)

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
    @PreAuthorize("hasAnyRole('REQUESTER','ADMIN')")
    @Operation(
        summary = "Submit workflow request",
        description = "Allows authenticated users to submit workflow requests"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Workflow request submitted successfully"
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
    public ApiResponse<WorkflowResponse> submitRequest(
            @Valid @RequestBody WorkflowSubmitRequest request,
            Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        WorkflowResponse workflowResponse = workflowService.submitRequest(request, currentUser);

        return new ApiResponse<>(
            true,
            "Workflow request submitted successfully",
            workflowResponse
        );
    }

    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "List of workflow requests retrieved successfully"
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

    //TODO: add workflow security to other endpoints too. 
    @GetMapping("/{id}")
    @PreAuthorize("@workflowSecurity.canViewRequest(#id, authentication)")
    public ApiResponse<WorkflowResponse> getRequest(@PathVariable Long id) {
        WorkflowResponse workflowResponse = workflowService.getRequestById(id);
        
        return new ApiResponse<>(
                true,
                "Workflow request retrieved",
                workflowResponse
        );
    }



    @GetMapping("/my-requests")
    @Operation(
        summary = "Get my workflow requests",
        description = "Retrieves a list of workflow requests submitted by the authenticated user"
    )
    public ApiResponse<List<WorkflowResponse>> getMyRequests(Authentication authentication){
        User currentUser = getCurrentUser(authentication);

        List<WorkflowResponse> workflowResponses = workflowService.getMyRequests(currentUser);

        return new ApiResponse<>(
            true,
            "List of request",
            workflowResponses
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
        summary = "Get workflow requests by status",
        description = "Retrieves a list of workflow requests based on their status"
    )
    public ApiResponse<List<WorkflowResponse>> getRequestByStatus(@PathVariable WorkflowStatus status) {
        List<WorkflowResponse> workflowResponses = workflowService.getRequestsByStatus(status);
        return new ApiResponse<>(
            true,
            "requests by status",
            workflowResponses
        );
    }
    
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    @Operation(
        summary = "Get workflow history",
        description = "Retrieves the history of a specific workflow request"
    )
    public ApiResponse<List<WorkflowHistoryResponse>> getWorkflowHistory(@PathVariable Long id) {
        List<WorkflowHistoryResponse> workflowHistoryResponses = workflowHistoryRepository
                .findByWorkflowRequestIdOrderByChangedAtAsc(id)
                .stream()
                .map(WorkflowHistoryMapper::toWorkflowHistoryResponse)
                .toList();

        return new ApiResponse<>(
            true,
            "Workflow history",
            workflowHistoryResponses
        );
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(
        summary = "Review workflow request",
        description = "Allows authenticated users to review a specific workflow request"
    )
    public ApiResponse<WorkflowResponse> reviewRequest(
            @PathVariable @NonNull Long id,
            Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        WorkflowResponse workflowResponse = workflowService.reviewRequest(id, currentUser);

        return new ApiResponse<>(
            true,
            "Workflow request marked under review",
            workflowResponse
        );
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
    @Operation(
        summary = "Approve workflow request",
        description = "Allows authenticated users to approve a specific workflow request"
    )
    public ApiResponse<WorkflowResponse> approveRequest(
            @PathVariable @NonNull Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        WorkflowResponse workflowResponse = workflowService.approveRequest(id, currentUser);

        return new ApiResponse<>(
            true,
            "Workflow request approved",
            workflowResponse
        );
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    @Operation(
        summary = "Reject workflow request",
        description = "Allows authenticated users to reject a specific workflow request"
    )
    public ApiResponse<WorkflowResponse> rejectRequest(
            @PathVariable @NonNull Long id,
            Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        
        WorkflowResponse workflowResponse = workflowService.rejectRequest(id, currentUser);

        return new ApiResponse<>(
            true,
            "Workflow request rejected",
            workflowResponse
        );

    }


    // Helpers 
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
