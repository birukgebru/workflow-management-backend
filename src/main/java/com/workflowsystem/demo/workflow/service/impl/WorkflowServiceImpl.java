package com.workflowsystem.demo.workflow.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.audit.repository.AuditLogRepository;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.entity.WorkflowHistory;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowAction;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;
import com.workflowsystem.demo.workflow.mapper.WorkflowRequestMapper;
import com.workflowsystem.demo.workflow.repository.WorkflowHistoryRepository;
import com.workflowsystem.demo.workflow.repository.WorkflowRequestRepository;
import com.workflowsystem.demo.workflow.service.WorkflowService;

@Service
public class WorkflowServiceImpl implements WorkflowService {
    private final WorkflowRequestRepository workflowRequestRepository;
    private final WorkflowHistoryRepository workflowHistoryRepository;
    private final AuditLogRepository auditLogRepository;

    public WorkflowServiceImpl(
            WorkflowRequestRepository workflowRequestRepository,
            WorkflowHistoryRepository workflowHistoryRepository,
            AuditLogRepository auditLogRepository) {
        this.workflowRequestRepository = workflowRequestRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public WorkflowResponse submitRequest(WorkflowSubmitRequest submitRequest, User currentUser) {
        WorkflowRequest workflowRequest = new WorkflowRequest();
        workflowRequest.setTitle(submitRequest.getTitle());
        workflowRequest.setDescription(submitRequest.getDescription());
        workflowRequest.setSubmittedBy(currentUser);
        workflowRequest.setStatus(WorkflowStatus.PENDING);
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logWorkflowHistory(
            savedWorkflowRequest,
            savedWorkflowRequest.getStatus(),
            savedWorkflowRequest.getStatus(),
            WorkflowAction.SUBMITTED,
            savedWorkflowRequest.getSubmittedBy()
        );

        logAudit(
                "SUBMITTED",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUser,
                "Workflow request submitted"
        );
        
        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getMyRequests(User currentUser){
        return workflowRequestRepository.findBySubmittedByOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(WorkflowRequestMapper::toWorkflowResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getRequestsByStatus(WorkflowStatus status){
        return workflowRequestRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(WorkflowRequestMapper::toWorkflowResponse)
                .toList();
    }

    private void logWorkflowHistory(
        WorkflowRequest workflowRequest,
        WorkflowStatus previousStatus,
        WorkflowStatus newStatus,
        WorkflowAction action,
        User changedBy
    ){
        WorkflowHistory workflowHistory = new WorkflowHistory(
                workflowRequest,
                previousStatus,
                newStatus,
                changedBy,
                action
        );
        workflowHistoryRepository.save(workflowHistory);
    }

    private void logAudit(
            String action,
            String entityType,
            Long entityId,
            User performedBy,
            String details
    ) {
        AuditLog auditLog = new AuditLog(
                action,
                entityType,
                entityId,
                performedBy,
                details
        );
        auditLogRepository.save(auditLog);
    }
}
