package com.workflowsystem.demo.workflow.mapper;

import com.workflowsystem.demo.workflow.dto.WorkflowCommentResponse;
import com.workflowsystem.demo.workflow.entity.WorkflowComment;

public class WorkflowCommentMapper {
    private WorkflowCommentMapper() {}

    public static WorkflowCommentResponse toWorkflowCommentResponse(WorkflowComment workflowComment) {
        if (workflowComment == null) {
            return null;
        }

        return new WorkflowCommentResponse(
            workflowComment.getId(),
            workflowComment.getWorkflowRequest().getId(),
            workflowComment.getComment()
        );
    }
}
