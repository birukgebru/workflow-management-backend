package com.workflowsystem.demo.workflow.dto;

public class WorkflowCommentResponse {
    private Long id;

    private Long workflowRequestId;
    private String comment;

    public WorkflowCommentResponse() {}

    public WorkflowCommentResponse(Long id, Long workflowRequestId, String comment) {
        this.id = id;
        this.workflowRequestId = workflowRequestId;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Long getWorkflowRequestId() {
        return workflowRequestId;
    }

    public String getComment() {
        return comment;
    }


}
