package com.workflowsystem.demo.workflow.dto;

import java.time.LocalDateTime;

import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.workflow.enums.WorkflowAction;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;

public class WorkflowHistoryResponse {

    private WorkflowAction action;
    private WorkflowStatus previousStatus;
    private WorkflowStatus newStatus;
    private UserResponse changedBy;
    private LocalDateTime changedAt;

    public WorkflowHistoryResponse() {}

    public WorkflowHistoryResponse(
            WorkflowAction action,
            WorkflowStatus previousStatus,
            WorkflowStatus newStatus,
            UserResponse changedBy,
            LocalDateTime changedAt) {
        this.action = action;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public WorkflowAction getAction() {
        return action;
    }

    public WorkflowStatus getPreviousStatus() {
        return previousStatus;
    }

    public WorkflowStatus getNewStatus() {
        return newStatus;
    }

    public UserResponse getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
