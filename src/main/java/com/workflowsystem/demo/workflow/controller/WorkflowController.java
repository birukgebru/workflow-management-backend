package com.workflowsystem.demo.workflow.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.workflowsystem.demo.shared.response.ApiResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowDashboardResponse;
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
    private final WorkflowHistoryRepository workflowHistoryRepository;

    public WorkflowController(WorkflowService workflowService, WorkflowHistoryRepository workflowHistoryRepository) {
        this.workflowService = workflowService;
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

        WorkflowResponse workflowResponse = workflowService.submitRequest(request);

        return new ApiResponse<>(
            true,
            "Workflow request submitted successfully",
            workflowResponse
        );
    }

    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Workflow request retrieved successfully"
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

    @GetMapping("/dashboard") 
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get workflow requests information",
        description = "Retrieves workflow information for the authenticated user"
    )

    public ApiResponse<WorkflowDashboardResponse> getDashboard(Authentication authentication) {
        WorkflowDashboardResponse dashboardResponse = workflowService.getDashboardInfo();

        return new ApiResponse<>(
            true,
            "Dashboard information retrieved",
            dashboardResponse
        );
    }

    @GetMapping("/my-requests")
    @Operation(
        summary = "Get my workflow requests",
        description = "Retrieves a list of workflow requests submitted by the authenticated user"
    )
    public ApiResponse<List<WorkflowResponse>> getMyRequests(Authentication authentication){
        List<WorkflowResponse> workflowResponses = workflowService.getMyRequests();

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
    public ApiResponse<Page<WorkflowResponse>> getRequestByStatus(@PathVariable WorkflowStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WorkflowResponse> workflowResponses = workflowService.getRequestsByStatus(status, page, size);
        return new ApiResponse<>(
            true,
            "requests by status",
            workflowResponses
        );
    }

    @GetMapping("/search/{keyword}")
    public ApiResponse<Page<WorkflowResponse>> getRequestByTitle(@PathVariable String keyword, 
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "10") int size
    ){
        Page<WorkflowResponse> workflowResponses = workflowService.getRequestsByTitle(keyword, page, size);
        return new ApiResponse<>(
            true,
            "Search results by title",
            workflowResponses
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
        summary = "Search workflow requests by title or description",
        description = "Retrieves a list of workflow requests filtered by title or description"
    )
    public ApiResponse<Page<WorkflowResponse>> search(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Page<WorkflowResponse> workflowResponses = workflowService.getRequestsByTitleOrDescription(keyword, page, size);
        return new ApiResponse<>(
            true,
            "Search results by title",
            workflowResponses
        );
    }

    // TODO: add endpoint to get workflow requests by assigned reviewer. This will be useful for reviewers to see the requests assigned to them.
    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get workflow requests by reviewer",
        description = "Retrieves a list of workflow requests assigned to a specific reviewer"
    )
    public ApiResponse<Page<WorkflowResponse>> getRequestByReviewer(@PathVariable Long reviewerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkflowResponse> workflowResponses = workflowService.getRequestsByReviewer(reviewerId, page, size);
        return new ApiResponse<>(
            true,
            "requests by reviewer",
            workflowResponses
        );
    }

    @GetMapping("/approver/{approverId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get workflow requests by approver",
        description = "Retrieves a list of workflow requests assigned to a specific approver"
    )
    public ApiResponse<Page<WorkflowResponse>> getRequestByApprover(@PathVariable Long approverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkflowResponse> workflowResponses = workflowService.getRequestsByApprover(approverId, page, size);
        return new ApiResponse<>(
            true,
            "requests by approver",
            workflowResponses
        );
    }

    @GetMapping("/submitter/{submitterId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get workflow requests by submitter",
        description = "Retrieves a list of workflow requests submitted by a specific user"
    )
    public ApiResponse<Page<WorkflowResponse>> getRequestBySubmitter(@PathVariable Long submitterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkflowResponse> workflowResponses = workflowService.getRequestsBySubmitter(submitterId, page, size);
        return new ApiResponse<>(
            true,
            "requests by submitter",
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

    @GetMapping
    @Operation(
        summary = "Get All Workflow requests",
        description = "Retrieves a list of all workflow requests"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<WorkflowResponse>> getAllWorkflowRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkflowResponse> workflowResponses = workflowService.getAllWorkflowRequests(page, size);
        return new ApiResponse<>(
                true,
                "All workflow requests retrieved",
                workflowResponses
        );
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(
        summary = "Review workflow request",
        description = "Allows authenticated users to review a specific workflow request"
    )
    public ApiResponse<WorkflowResponse> reviewRequest(@PathVariable @NonNull Long id, Authentication authentication) {
        WorkflowResponse workflowResponse = workflowService.reviewRequest(id);

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
    public ApiResponse<WorkflowResponse> approveRequest(@PathVariable @NonNull Long id, Authentication authentication) {

        WorkflowResponse workflowResponse = workflowService.approveRequest(id);

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
    public ApiResponse<WorkflowResponse> rejectRequest(@PathVariable @NonNull Long id, Authentication authentication) {
        WorkflowResponse workflowResponse = workflowService.rejectRequest(id);

        return new ApiResponse<>(
            true,
            "Workflow request rejected",
            workflowResponse
        );

    }

    @PutMapping("/{id}/assign-reviewer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Assign reviewer to workflow request",
        description = "Allows admin users to assign a reviewer to a specific workflow request"
    )
    public ApiResponse<WorkflowResponse> assignReviewer(
            @PathVariable @NonNull Long id,
            @RequestParam @NonNull Long reviewerId, 
            Authentication authentication) {
        WorkflowResponse workflowResponse = workflowService.assignReviewer(id, reviewerId);

        return new ApiResponse<>(
            true,
            "Reviewer assigned to workflow request",
            workflowResponse
        );
    }

    @PutMapping("/{id}/assign-approver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Assign approver to a workflow",
        description = "Allows admin user to assign an approver to a workflow request"
    )
    public ApiResponse<WorkflowResponse> assignApprover(
            @PathVariable @NonNull Long id,
            @RequestParam @NonNull Long approverId,
            Authentication authentication) {

        WorkflowResponse workflowResponse = workflowService.assignApprover(id, approverId);

        return new ApiResponse<>(
            true,
            "Approver assigned to workflow request",
            workflowResponse
        );
    }

}
