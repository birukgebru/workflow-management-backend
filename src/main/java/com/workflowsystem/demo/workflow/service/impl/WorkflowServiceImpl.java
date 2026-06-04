package com.workflowsystem.demo.workflow.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.audit.repository.AuditLogRepository;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.auth.repository.UserRepository;
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
import com.workflowsystem.demo.workflow.state.WorkflowStateMachine;

@Service
public class WorkflowServiceImpl implements WorkflowService {
    private final WorkflowRequestRepository workflowRequestRepository;
    private final WorkflowHistoryRepository workflowHistoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final WorkflowStateMachine workflowStateMachine;

    public WorkflowServiceImpl(
            WorkflowRequestRepository workflowRequestRepository,
            WorkflowHistoryRepository workflowHistoryRepository,
            AuditLogRepository auditLogRepository,
            UserRepository userRepository,
            WorkflowStateMachine workflowStateMachine) {
        this.workflowRequestRepository = workflowRequestRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.workflowStateMachine = workflowStateMachine;
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
    public List<WorkflowResponse> getAllWorkflowRequests() {
        return workflowRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(WorkflowRequestMapper::toWorkflowResponse)
                .toList();
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

        workflowStateMachine.validateTransition(previousStatus, WorkflowStatus.UNDER_REVIEW);

        if(workflowRequest.getAssignedReviewer() == null || !workflowRequest.getAssignedReviewer().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the assigned reviewer for this request");
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

        if(workflowRequest.getAssignedApprover() == null || !workflowRequest.getAssignedApprover().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the assigned approver for this request");
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

        if(workflowRequest.getAssignedApprover() == null || !workflowRequest.getAssignedApprover().getId().equals(currentUser.getId())) {
                 throw new IllegalStateException("You are not the assigned approver for this request to reject");
        }

        workflowRequest.setStatus(WorkflowStatus.REJECTED);
        workflowRequest.setRejectedBy(currentUser);
        workflowRequest.setRejectedAt(LocalDateTime.now());
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logWorkflowHistory(savedWorkflowRequest, previousStatus, savedWorkflowRequest.getStatus(), WorkflowAction.REJECTED, currentUser);

        logAudit("REJECTED", "WorkflowRequest", savedWorkflowRequest.getId(), currentUser, "Workflow request rejected");

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowResponse getRequestById(Long id) {
        WorkflowRequest workflowRequest =
                workflowRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Workflow request not found"));

        return WorkflowRequestMapper.toWorkflowResponse(workflowRequest);
    }

    @Override
    @Transactional
    public WorkflowResponse assignReviewer(Long requestId, Long reviewerId, User currentUser) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        User reviewer = userRepository.findUserById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        
        boolean hasReviewerRole = reviewer.getRoles()
                .stream()
                .anyMatch(
                    r -> r.getName() == Role.ROLE_REVIEWER
                );

        if(!hasReviewerRole){
            throw new IllegalArgumentException("User is not a reviewer");
        }
    
        workflowRequest.setAssignedReviewer(reviewer);
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logAudit(
                "ASSIGNED_REVIEWER",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUser,
                "Assigned reviewer with ID " + reviewerId
        );

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);

    }

    @Override
    @Transactional
    public WorkflowResponse assignApprover(Long requestId, Long approverId, User currentUser) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        User approver = userRepository.findUserById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));

        boolean hasApproverRole = approver.getRoles()
                .stream()                
                .anyMatch(
                    r -> r.getName() == Role.ROLE_APPROVER
                );

        if(!hasApproverRole){
            throw new IllegalArgumentException("User is not an approver");
        }

        workflowRequest.setAssignedApprover(approver);
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logAudit(
                "ASSIGNED_APPROVER",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUser,
                "Assigned approver with ID " + approverId
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
