package com.workflowsystem.demo.audit.dto;

import java.time.LocalDateTime;

import com.workflowsystem.demo.auth.dto.UserResponse;

public class AuditLogResponse {

    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private UserResponse performedBy;
    private String details;
    private LocalDateTime createdAt;

    public AuditLogResponse() {}

    public AuditLogResponse(
            Long id,
            String action,
            String entityType,
            Long entityId,
            UserResponse performedBy,
            String details,
            LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public UserResponse getPerformedBy() {
        return performedBy;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
