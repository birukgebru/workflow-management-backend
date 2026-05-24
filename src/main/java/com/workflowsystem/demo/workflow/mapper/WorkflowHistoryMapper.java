package com.workflowsystem.demo.workflow.mapper;

import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.workflow.dto.WorkflowHistoryResponse;
import com.workflowsystem.demo.workflow.entity.WorkflowHistory;

public final class WorkflowHistoryMapper {

    private WorkflowHistoryMapper() {}

    public static WorkflowHistoryResponse toWorkflowHistoryResponse(WorkflowHistory workflowHistory) {
        if (workflowHistory == null) {
            return null;
        }

        return new WorkflowHistoryResponse(
            workflowHistory.getAction(),
            workflowHistory.getPreviousStatus(),
            workflowHistory.getNewStatus(),
            toUserResponse(workflowHistory.getChangedBy()),
            workflowHistory.getChangedAt()
        );
    }

    private static UserResponse toUserResponse(User user) {
        return user == null ? null : UserMapper.toUserResponse(user);
    }
}
