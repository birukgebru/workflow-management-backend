package com.workflowsystem.demo.notification.event;

public class ApproverAssignedEvent {

    private final Long workflowId;
    private final String approverEmail;

    public ApproverAssignedEvent(Long workflowId, String approverEmail) {
        this.workflowId = workflowId;
        this.approverEmail = approverEmail;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public String getApproverEmail() {
        return approverEmail;
    }
}