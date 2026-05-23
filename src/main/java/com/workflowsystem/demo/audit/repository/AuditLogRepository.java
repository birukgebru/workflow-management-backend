package com.workflowsystem.demo.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workflowsystem.demo.audit.entitiy.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
