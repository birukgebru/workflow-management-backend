package com.workflowsystem.demo.workflow.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workflowsystem.demo.audit.service.AuditLogService;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.workflow.dto.WorkflowDashboardResponse;
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
    private final UserRepository userRepository;
    private final WorkflowStateMachine workflowStateMachine;
    private final AuditLogService auditLogService;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowServiceImpl.class);


    public WorkflowServiceImpl(
            WorkflowRequestRepository workflowRequestRepository,
            WorkflowHistoryRepository workflowHistoryRepository,
            UserRepository userRepository,
            WorkflowStateMachine workflowStateMachine,
            AuditLogService auditLogService) {
        this.workflowRequestRepository = workflowRequestRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
        this.userRepository = userRepository;
        this.workflowStateMachine = workflowStateMachine;
        this.auditLogService = auditLogService;
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
        logger.info("Workflow request submitted by user {} with request ID {}", currentUser.getId(), savedWorkflowRequest.getId());
        logWorkflowHistory(
            savedWorkflowRequest,
            null,
            savedWorkflowRequest.getStatus(),
            WorkflowAction.SUBMITTED,
            savedWorkflowRequest.getSubmittedBy()
        );

        auditLogService.logAudit(
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
    public Page<WorkflowResponse> getAllWorkflowRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkflowRequest> workflowRequests = workflowRequestRepository.findAllByOrderByCreatedAtDesc(pageable);

        return workflowRequests.map(WorkflowRequestMapper::toWorkflowResponse);
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
    public Page<WorkflowResponse> getRequestsByStatus(WorkflowStatus status, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkflowRequest> workflowRequests = workflowRequestRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return workflowRequests.map(WorkflowRequestMapper::toWorkflowResponse);
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

        logger.info("Workflow request with ID {} marked under review by user {}", savedWorkflowRequest.getId(), currentUser.getId());
        logWorkflowHistory(
                savedWorkflowRequest,
                previousStatus,
                savedWorkflowRequest.getStatus(),
                WorkflowAction.REVIEWED,
                currentUser
        );

        auditLogService.logAudit(
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
        workflowStateMachine.validateTransition(previousStatus, WorkflowStatus.APPROVED);

        if(workflowRequest.getAssignedApprover() == null || !workflowRequest.getAssignedApprover().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the assigned approver for this request");
        }

        workflowRequest.setStatus(WorkflowStatus.APPROVED);
        workflowRequest.setApprovedBy(currentUser);
        workflowRequest.setApprovedAt(LocalDateTime.now());
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logger.info("Workflow request with ID {} approved by user {}", savedWorkflowRequest.getId(), currentUser.getId());

        logWorkflowHistory(
                savedWorkflowRequest,
                previousStatus,
                savedWorkflowRequest.getStatus(),
                WorkflowAction.APPROVED,
                currentUser
        );

        auditLogService.logAudit(
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
        workflowStateMachine.validateTransition(previousStatus, WorkflowStatus.REJECTED);

        if(workflowRequest.getAssignedApprover() == null || !workflowRequest.getAssignedApprover().getId().equals(currentUser.getId())) {
                 throw new IllegalStateException("You are not the assigned approver for this request to reject");
        }

        workflowRequest.setStatus(WorkflowStatus.REJECTED);
        workflowRequest.setRejectedBy(currentUser);
        workflowRequest.setRejectedAt(LocalDateTime.now());
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logger.info("Workflow request with ID {} rejected by user {}", savedWorkflowRequest.getId(), currentUser.getId());
        logWorkflowHistory(savedWorkflowRequest, previousStatus, savedWorkflowRequest.getStatus(), WorkflowAction.REJECTED, currentUser);

        auditLogService.logAudit("REJECTED", "WorkflowRequest", savedWorkflowRequest.getId(), currentUser, "Workflow request rejected");

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

        auditLogService.logAudit(
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

        auditLogService.logAudit(
                "ASSIGNED_APPROVER",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUser,
                "Assigned approver with ID " + approverId
        );

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    public WorkflowDashboardResponse getDashboardInfo(User currentUser) {

        WorkflowDashboardResponse WorkflowDashboardResponse = new WorkflowDashboardResponse(
                workflowRequestRepository.countAll(),
                workflowRequestRepository.countByStatus(WorkflowStatus.PENDING),
                workflowRequestRepository.countByStatus(WorkflowStatus.UNDER_REVIEW),
                workflowRequestRepository.countByStatus(WorkflowStatus.APPROVED),
                workflowRequestRepository.countByStatus(WorkflowStatus.REJECTED)
        );
        return WorkflowDashboardResponse;
        

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
}
