package com.workflowsystem.demo.audit.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.audit.dto.AuditLogResponse;
import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.audit.mapper.AuditLogMapper;
import com.workflowsystem.demo.audit.repository.AuditLogRepository;
import com.workflowsystem.demo.audit.service.AuditLogService;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.shared.response.ApiResponse;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;

    public AuditController(AuditLogRepository auditLogRepository, AuditLogService auditLogService) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<List<AuditLogResponse>> getAuditLogs() {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogs().stream()
                .map(AuditLogMapper::toAuditLogResponse)
                .toList();

        return new ApiResponse<>(
                true,
                "Audit logs",
                auditLogs
        );
    }

}
