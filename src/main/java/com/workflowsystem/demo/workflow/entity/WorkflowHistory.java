package com.workflowsystem.demo.workflow.entity;

import java.time.LocalDateTime;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.workflow.enums.WorkflowAction;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "workflow_histories",
        indexes = {
            @Index(name = "idx_history_workflow", columnList = "workflow_request_id"),
            @Index(name = "idx_history_changed_at", columnList = "changed_at"),
            @Index(name = "idx_history_changed_by", columnList = "changed_by_id")
        }
)
public class WorkflowHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_request_id", nullable = false)
    private WorkflowRequest workflowRequest;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private WorkflowStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private WorkflowStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowAction action; 

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public WorkflowHistory() {}

    public WorkflowHistory(
            WorkflowRequest workflowRequest,
            WorkflowStatus previousStatus,
            WorkflowStatus newStatus,
            User changedBy,
            WorkflowAction action) {
        this.workflowRequest = workflowRequest;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.action = action;
    }

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public WorkflowRequest getWorkflowRequest() {
        return workflowRequest;
    }

    public void setWorkflowRequest(WorkflowRequest workflowRequest) {
        this.workflowRequest = workflowRequest;
    }

    public WorkflowStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(WorkflowStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public WorkflowStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(WorkflowStatus newStatus) {
        this.newStatus = newStatus;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public WorkflowAction getAction() {
        return action;
    }

    public void setAction(WorkflowAction action) {
        this.action = action;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
