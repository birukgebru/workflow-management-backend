package com.workflowsystem.demo.workflow.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.audit.repository.AuditLogRepository;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.shared.exception.InvalidWorkflowStateException;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
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
            null,
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

    @Override
    @Transactional
    public WorkflowResponse reviewRequest(@NonNull Long requestId, User currentUser) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));

        WorkflowStatus previousStatus = workflowRequest.getStatus();
        if (previousStatus != WorkflowStatus.PENDING) {
            throw new InvalidWorkflowStateException("Only pending requests can be reviewed");
        }

        workflowRequest.setStatus(WorkflowStatus.UNDER_REVIEW);
        workflowRequest.setReviewedBy(currentUser);
        workflowRequest.setReviewedAt(LocalDateTime.now());

        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logWorkflowHistory(
                savedWorkflowRequest,
                previousStatus,
                savedWorkflowRequest.getStatus(),
                WorkflowAction.REVIEWED,
                currentUser
        );

        logAudit(
                "REVIEWED",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUser,
                "Workflow request marked under review"
        );

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional
    public WorkflowResponse approveRequest(@NonNull Long requestId, User currentUser) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        WorkflowStatus previousStatus = workflowRequest.getStatus();
        if (previousStatus != WorkflowStatus.UNDER_REVIEW) {
            throw new InvalidWorkflowStateException("Only workflow requests under review can be approved");
        }

        workflowRequest.setStatus(WorkflowStatus.APPROVED);
        workflowRequest.setApprovedBy(currentUser);
        workflowRequest.setApprovedAt(LocalDateTime.now());
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logWorkflowHistory(
                savedWorkflowRequest,
                previousStatus,
                savedWorkflowRequest.getStatus(),
                WorkflowAction.APPROVED,
                currentUser
        );

        logAudit(
                "APPROVED",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUser,
                "Workflow request approved"
        );

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional
    public WorkflowResponse rejectRequest(@NonNull Long requestId, User currentUser) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        WorkflowStatus previousStatus = workflowRequest.getStatus();
        if (previousStatus != WorkflowStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Only workflow requests under review can be rejected");
        }

        workflowRequest.setStatus(WorkflowStatus.REJECTED);
        workflowRequest.setRejectedBy(currentUser);
        workflowRequest.setRejectedAt(LocalDateTime.now());
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logWorkflowHistory(
                savedWorkflowRequest,
                previousStatus,
                savedWorkflowRequest.getStatus(),
                WorkflowAction.REJECTED,
                currentUser
        );

        logAudit(
                "REJECTED",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUser,
                "Workflow request rejected"
        );

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
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
