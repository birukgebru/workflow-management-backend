package com.workflowsystem.demo.workflow.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.shared.exception.InvalidWorkflowCommentException;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.workflow.dto.WorkflowCommentResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
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

    public WorkflowCommentService(WorkflowCommentRepository workflowCommentRepository, WorkflowRequestRepository workflowRequestRepository) {
        this.workflowCommentRepository = workflowCommentRepository;
        this.workflowRequestRepository = workflowRequestRepository;
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

       

        return WorkflowCommentMapper.toWorkflowCommentResponse(savedComment);
    }



    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.ROLE_ADMIN);
    }

    private boolean isWorkflowParticipant(WorkflowRequest workflowRequest, User user) {
        Long userId = user.getId();
        return hasUserId(workflowRequest.getAssignedApprover(), userId)
                || hasUserId(workflowRequest.getAssignedReviewer(), userId)
                || hasUserId(workflowRequest.getSubmittedBy(), userId);
    }

    private boolean hasUserId(User user, Long userId) {
        return user != null && Objects.equals(user.getId(), userId);
    }
}
