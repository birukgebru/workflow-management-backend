package com.workflowsystem.demo.workflow.dto;

import java.time.LocalDateTime;

import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;

public class WorkflowResponse {
    private Long id;
    private String title;
    private String description;
    private WorkflowStatus status;
    private UserResponse submittedBy;
    private UserResponse reviewedBy;
    private UserResponse assignedReviewer;
    private UserResponse approvedBy;
    private UserResponse assignedApprover;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime approvedAt;

    public WorkflowResponse() {}

    public WorkflowResponse(
            Long id,
            String title,
            String description,
            WorkflowStatus status,
            UserResponse submittedBy,
            UserResponse reviewedBy,
            UserResponse approvedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime reviewedAt,
            LocalDateTime approvedAt,
            UserResponse assignedReviewer,
            UserResponse assignedApprover
        ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.submittedBy = submittedBy;
        this.reviewedBy = reviewedBy;
        this.approvedBy = approvedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewedAt = reviewedAt;
        this.approvedAt = approvedAt;
        this.assignedApprover = assignedApprover;
        this.assignedReviewer = assignedReviewer;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public UserResponse getSubmittedBy() {
        return submittedBy;
    }

    public UserResponse getAssignedReviewer(){
        return assignedReviewer;
    }

    public UserResponse getAssignedApprover(){
        return assignedApprover;
    }

    public UserResponse getReviewedBy() {
        return reviewedBy;
    }

    public UserResponse getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
}
