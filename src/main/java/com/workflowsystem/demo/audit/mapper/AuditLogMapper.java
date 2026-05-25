package com.workflowsystem.demo.audit.mapper;

import com.workflowsystem.demo.audit.dto.AuditLogResponse;
import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.auth.mapper.UserMapper;

public final class AuditLogMapper {

    private AuditLogMapper() {}

    public static AuditLogResponse toAuditLogResponse(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                UserMapper.toUserResponse(auditLog.getPerformedBy()), 
                auditLog.getDetails(),
                auditLog.getCreatedAt()
        );

    }
    
}
