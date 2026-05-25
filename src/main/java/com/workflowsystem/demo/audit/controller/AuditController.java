package com.workflowsystem.demo.audit.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.audit.dto.AuditLogResponse;
import com.workflowsystem.demo.audit.mapper.AuditLogMapper;
import com.workflowsystem.demo.audit.service.AuditLogService;
import com.workflowsystem.demo.shared.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/audit")
@SecurityRequirement(name = "bearerAuth")
@Tag(
    name = "Audit Logs",
    description = "Endpoints for managing audit logs"
)
public class AuditController {

    private final AuditLogService auditLogService;

    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Get audit logs",
        description = "Retrieves a list of audit logs"
    )
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
