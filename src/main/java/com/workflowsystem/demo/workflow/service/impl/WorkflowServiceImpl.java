package com.workflowsystem.demo.workflow.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
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
import com.workflowsystem.demo.auth.service.CurrentUserService;
import com.workflowsystem.demo.notification.event.ApproverAssignedEvent;
import com.workflowsystem.demo.notification.event.ReviewerAssignedEvent;
import com.workflowsystem.demo.notification.event.WorkflowApprovedEvent;
import com.workflowsystem.demo.notification.event.WorkflowRejectedEvent;
import com.workflowsystem.demo.notification.event.WorkflowSubmittedEvent;
import com.workflowsystem.demo.shared.exception.InvalidWorkflowStateException;
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
    private final ApplicationEventPublisher eventPublisher;
    private final CurrentUserService currentUserService;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    public WorkflowServiceImpl(
            WorkflowRequestRepository workflowRequestRepository,
            WorkflowHistoryRepository workflowHistoryRepository,
            UserRepository userRepository,
            WorkflowStateMachine workflowStateMachine,
            AuditLogService auditLogService, 
            ApplicationEventPublisher eventPublisher,
            CurrentUserService currentUserService
        ) {
        this.workflowRequestRepository = workflowRequestRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
        this.userRepository = userRepository;
        this.workflowStateMachine = workflowStateMachine;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public WorkflowResponse submitRequest(WorkflowSubmitRequest submitRequest) {
        User currentUser = currentUserService.getCurrentUser();
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
        
        eventPublisher.publishEvent(new WorkflowSubmittedEvent(savedWorkflowRequest.getId(), currentUser.getEmail()));

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
    public List<WorkflowResponse> getMyRequests(){
        return workflowRequestRepository.findBySubmittedByOrderByCreatedAtDesc(currentUserService.getCurrentUser())
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
    @Transactional(readOnly = true)
    public Page<WorkflowResponse> getRequestsByTitle(String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkflowRequest> workflowRequests = workflowRequestRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return workflowRequests.map(WorkflowRequestMapper::toWorkflowResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkflowResponse> getRequestsByTitleOrDescription(String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkflowRequest> workflowRequests = workflowRequestRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
   
        return workflowRequests.map(WorkflowRequestMapper::toWorkflowResponse);
    }

    @Override
    @Transactional(readOnly = true) 
    public Page<WorkflowResponse> getRequestsByReviewer(Long reviewerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkflowRequest> workflowRequests = workflowRequestRepository.findByAssignedReviewerId(reviewerId, pageable);
        return workflowRequests.map(WorkflowRequestMapper::toWorkflowResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkflowResponse> getRequestsByApprover(Long approverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkflowRequest> workflowRequests = workflowRequestRepository.findByAssignedApproverId(approverId, pageable);
        return workflowRequests.map(WorkflowRequestMapper::toWorkflowResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkflowResponse> getRequestsBySubmitter(Long submittedById, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkflowRequest> workflowRequests = workflowRequestRepository.findBySubmittedById(submittedById, pageable);
        return workflowRequests.map(WorkflowRequestMapper::toWorkflowResponse);
}

    @Override
    @Transactional
    public WorkflowResponse reviewRequest(@NonNull Long requestId) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));

        WorkflowStatus previousStatus = workflowRequest.getStatus();
        workflowStateMachine.validateTransition(previousStatus, WorkflowStatus.UNDER_REVIEW);

        if(workflowRequest.getAssignedReviewer() == null || !workflowRequest.getAssignedReviewer().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new InvalidWorkflowStateException("You are not the assigned reviewer for this request");
        }

        workflowRequest.setStatus(WorkflowStatus.UNDER_REVIEW);
        workflowRequest.setReviewedBy(currentUserService.getCurrentUser());
        workflowRequest.setReviewedAt(LocalDateTime.now());

        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logger.info("Workflow request with ID {} marked under review by user {}", savedWorkflowRequest.getId(), currentUserService.getCurrentUser().getId());
        logWorkflowHistory(
                savedWorkflowRequest,
                previousStatus,
                savedWorkflowRequest.getStatus(),
                WorkflowAction.REVIEWED,
                currentUserService.getCurrentUser()
        );

        auditLogService.logAudit(
                "REVIEWED",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUserService.getCurrentUser(),
                "Workflow request marked under review"
        );

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional
    public WorkflowResponse approveRequest(@NonNull Long requestId) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        WorkflowStatus previousStatus = workflowRequest.getStatus();
        workflowStateMachine.validateTransition(previousStatus, WorkflowStatus.APPROVED);

        if(workflowRequest.getAssignedApprover() == null || !workflowRequest.getAssignedApprover().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new InvalidWorkflowStateException("You are not the assigned approver for this request");
        }

        workflowRequest.setStatus(WorkflowStatus.APPROVED);
        workflowRequest.setApprovedBy(currentUserService.getCurrentUser());
        workflowRequest.setApprovedAt(LocalDateTime.now());
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);
        
        logger.info("Workflow request with ID {} approved by user {}", savedWorkflowRequest.getId(), currentUserService.getCurrentUser().getId());

        logWorkflowHistory(
                savedWorkflowRequest,
                previousStatus,
                savedWorkflowRequest.getStatus(),
                WorkflowAction.APPROVED,
                currentUserService.getCurrentUser()
        );

        auditLogService.logAudit(
                "APPROVED",
                "WorkflowRequest",
                savedWorkflowRequest.getId(),
                currentUserService.getCurrentUser(),
                "Workflow request approved"
        );

        //TODO get email 
        String requesterE = "biruk.gebru28@gmail.com";
        eventPublisher.publishEvent(new WorkflowApprovedEvent(savedWorkflowRequest.getId(), requesterE));

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional
    public WorkflowResponse rejectRequest(@NonNull Long requestId) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        WorkflowStatus previousStatus = workflowRequest.getStatus();
        workflowStateMachine.validateTransition(previousStatus, WorkflowStatus.REJECTED);

        if(workflowRequest.getAssignedApprover() == null || !workflowRequest.getAssignedApprover().getId().equals(currentUserService.getCurrentUser().getId())) {
                 throw new InvalidWorkflowStateException("You are not the assigned approver for this request to reject");
        }

        workflowRequest.setStatus(WorkflowStatus.REJECTED);
        workflowRequest.setRejectedBy(currentUserService.getCurrentUser());
        workflowRequest.setRejectedAt(LocalDateTime.now());
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);

        logger.info("Workflow request with ID {} rejected by user {}", savedWorkflowRequest.getId(), currentUserService.getCurrentUser().getId());
        logWorkflowHistory(savedWorkflowRequest, previousStatus, savedWorkflowRequest.getStatus(), WorkflowAction.REJECTED, currentUserService.getCurrentUser());
        auditLogService.logAudit("REJECTED", "WorkflowRequest", savedWorkflowRequest.getId(), currentUserService.getCurrentUser(), "Workflow request rejected");

        //TODO get email 
        String requesterE = "biruk.gebru28@gmail.com";
        eventPublisher.publishEvent(new WorkflowRejectedEvent(savedWorkflowRequest.getId(), requesterE));
        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowResponse getRequestById(Long id) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(id)
                                            .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));

        return WorkflowRequestMapper.toWorkflowResponse(workflowRequest);
    }

    @Override
    @Transactional
    public WorkflowResponse assignReviewer(Long requestId, Long reviewerId) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        User reviewer = userRepository.findUserById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        boolean hasReviewerRole = reviewer.getRoles()
                .stream()
                .anyMatch(r -> r.getName() == Role.ROLE_REVIEWER);

        if(!hasReviewerRole){
            throw new InvalidWorkflowStateException("User is not a reviewer");
        }
        workflowRequest.setAssignedReviewer(reviewer);
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);
        auditLogService.logAudit("ASSIGNED_REVIEWER", "WorkflowRequest", savedWorkflowRequest.getId(), currentUserService.getCurrentUser(), "Assigned reviewer with ID " + reviewerId);
        eventPublisher.publishEvent(new ReviewerAssignedEvent(savedWorkflowRequest.getId(), reviewer.getEmail()));
        
        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional
    public WorkflowResponse assignApprover(Long requestId, Long approverId) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        User approver = userRepository.findUserById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));
        boolean hasApproverRole = approver.getRoles()
                                          .stream()        
                                          .anyMatch(r -> r.getName() == Role.ROLE_APPROVER);

        if(!hasApproverRole){
            throw new InvalidWorkflowStateException("User is not an approver");
        }

        workflowRequest.setAssignedApprover(approver);
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);
        auditLogService.logAudit("ASSIGNED_APPROVER", "WorkflowRequest", savedWorkflowRequest.getId(), currentUserService.getCurrentUser(), "Assigned approver with ID " + approverId);
        eventPublisher.publishEvent(new ApproverAssignedEvent(savedWorkflowRequest.getId(), approver.getEmail()));

        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    public WorkflowDashboardResponse getDashboardInfo() {

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
