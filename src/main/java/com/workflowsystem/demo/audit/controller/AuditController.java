package com.workflowsystem.demo.audit.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.audit.dto.AuditLogResponse;
import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.audit.repository.AuditLogRepository;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.shared.response.ApiResponse;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<List<AuditLogResponse>> getAuditLogs() {
        List<AuditLogResponse> auditLogs = auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toAuditLogResponse)
                .toList();

        return new ApiResponse<>(
                true,
                "Audit logs",
                auditLogs
        );
    }

    private AuditLogResponse toAuditLogResponse(AuditLog auditLog) {
        User performedBy = auditLog.getPerformedBy();

        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                performedBy == null ? null : UserMapper.toUserResponse(performedBy),
                auditLog.getDetails(),
                auditLog.getCreatedAt()
        );
    }
}
