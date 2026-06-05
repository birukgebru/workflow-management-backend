package com.workflowsystem.demo.workflow.controller;

import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowCommentRequest;
import com.workflowsystem.demo.workflow.dto.WorkflowCommentResponse;
import com.workflowsystem.demo.workflow.service.WorkflowCommentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/workflow")
@SecurityRequirement(name = "bearerAuth")
@Tag(
    name = "Workflow Comment Management",
    description = "Endpoints for managing workflow comments"
)
public class WorkflowCommentController {
    private final WorkflowCommentService workflowCommentService;
    private final UserRepository userRepository;

    public WorkflowCommentController(WorkflowCommentService workflowCommentService, UserRepository userRepository) {
        this.workflowCommentService = workflowCommentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{workflowRequestId}/comments")
    @Operation(summary = "Add a comment to a workflow request", description = "Allows approvers, reviewers, and the requests to add comments to a workflow request.")
    public ApiResponse<WorkflowCommentResponse> addComment(@PathVariable Long workflowRequestId, @Valid @RequestBody WorkflowCommentRequest commentRequest, Authentication currentUser) {
        WorkflowCommentResponse response = workflowCommentService.addComment(workflowRequestId, commentRequest.getComment(), getCurrentUser(currentUser));

        return new ApiResponse<>(
                true,
                "Comment added successfully",
                response
        );
    }

      // Helpers 
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
