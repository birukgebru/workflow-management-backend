package com.workflowsystem.demo.workflow.service;

import java.util.List;
import java.util.Objects;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService.Work;
import org.springframework.stereotype.Service;

import com.workflowsystem.demo.audit.service.AuditLogService;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.shared.exception.InvalidWorkflowCommentException;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.workflow.dto.WorkflowCommentResponse;
import com.workflowsystem.demo.workflow.entity.WorkflowComment;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;
import com.workflowsystem.demo.workflow.mapper.WorkflowCommentMapper;
import com.workflowsystem.demo.workflow.repository.WorkflowCommentRepository;
import com.workflowsystem.demo.workflow.repository.WorkflowRequestRepository;

import jakarta.transaction.Transactional;

@Service
public class WorkflowCommentService {
    private final WorkflowCommentRepository workflowCommentRepository;
    private final WorkflowRequestRepository workflowRequestRepository;
    private final AuditLogService auditLogService;

    public WorkflowCommentService(
        WorkflowCommentRepository workflowCommentRepository, 
        WorkflowRequestRepository workflowRequestRepository,
        AuditLogService auditLogService) {
        this.workflowCommentRepository = workflowCommentRepository;
        this.workflowRequestRepository = workflowRequestRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public WorkflowCommentResponse addComment(Long requestId, String comment, User currentUser) {
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId)
                                          .orElseThrow(() -> 
                                          new ResourceNotFoundException("Workflow request not found"));
        if (!isAdmin(currentUser) && !isWorkflowParticipant(workflowRequest, currentUser)) {
            throw new InvalidWorkflowCommentException("You are not authorized to comment on this workflow request");
        }

        WorkflowComment workflowComment = new WorkflowComment();
        workflowComment.setWorkflowRequest(workflowRequest);
        workflowComment.setComment(comment);
        workflowComment.setCommenter(currentUser);
        WorkflowComment savedComment = workflowCommentRepository.save(workflowComment);

        auditLogService.logAudit(
            "ADDED_COMMENT", 
            "Comment", 
            savedComment.getId(), 
            currentUser, "Added comment to workflow request"
        );

        return WorkflowCommentMapper.toWorkflowCommentResponse(savedComment);
    }

    public List<WorkflowCommentResponse> getCommentById(Long workflowRequestId, User currentUser) {
        WorkflowRequest request = workflowRequestRepository.findById(workflowRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));    

          if (!isAdmin(currentUser) && !isWorkflowParticipant(request, currentUser)) {
            throw new InvalidWorkflowCommentException("You are not authorized to comment on this workflow request");
        }

        List<WorkflowComment> comments = workflowCommentRepository.findByWorkflowRequestIdOrderByIdAsc(request.getId());
        return comments.stream()
                .map(WorkflowCommentMapper::toWorkflowCommentResponse)
                .toList();
        
    }



    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.ROLE_ADMIN);
    }

    private boolean isWorkflowParticipant(WorkflowRequest workflowRequest, User user) {
        Long userId = user.getId();
        if(userId == null) {
            return false;
        }
        return hasUserId(workflowRequest.getAssignedApprover(), userId)
                || hasUserId(workflowRequest.getAssignedReviewer(), userId)
                || hasUserId(workflowRequest.getSubmittedBy(), userId);
    }

    private boolean hasUserId(User user, Long userId) {
        if(user.getId() == null) {
            return false;
        }
        return user != null && Objects.equals(user.getId(), userId);
    }
}
