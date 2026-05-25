package com.workflowsystem.demo.audit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.workflowsystem.demo.audit.dto.AuditLogResponse;
import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.audit.mapper.AuditLogMapper;
import com.workflowsystem.demo.audit.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getAuditLogs() {
        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        return auditLogs;
    }
}
