package com.workflowsystem.demo.workflow.dto;

public class WorkflowHistoryRequest {
    private Long workflowRequestId;

    public WorkflowHistoryRequest() {}

    public WorkflowHistoryRequest(Long workflowRequestId) {
        this.workflowRequestId = workflowRequestId;
    }

    public Long getWorkflowRequestId() {
        return workflowRequestId;
    }

    public void setWorkflowRequestId(Long workflowRequestId) {
        this.workflowRequestId = workflowRequestId;
    }
    
}
