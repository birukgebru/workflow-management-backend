package com.workflowsystem.demo.workflow.mapper;

import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;

public final class WorkflowRequestMapper {

    private WorkflowRequestMapper() {}

    public static WorkflowResponse toWorkflowResponse(WorkflowRequest workflowRequest) {
        if (workflowRequest == null) {
            return null;
        }

        return new WorkflowResponse(
            workflowRequest.getId(),
            workflowRequest.getTitle(),
            workflowRequest.getDescription(),
            workflowRequest.getStatus(),
            toUserResponse(workflowRequest.getSubmittedBy()),
            toUserResponse(workflowRequest.getReviewedBy()),
            toUserResponse(workflowRequest.getApprovedBy()),
            workflowRequest.getCreatedAt(),
            workflowRequest.getUpdatedAt(),
            workflowRequest.getReviewedAt(),
            workflowRequest.getApprovedAt(),
            toUserResponse(workflowRequest.getAssignedReviewer()),
            toUserResponse(workflowRequest.getAssignedApprover())
        );
    }

    private static UserResponse toUserResponse(User user) {
        return user == null ? null : UserMapper.toUserResponse(user);
    }
}
