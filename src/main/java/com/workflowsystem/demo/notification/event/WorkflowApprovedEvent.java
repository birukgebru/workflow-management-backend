package com.workflowsystem.demo.notification.event;

public class WorkflowApprovedEvent {

    private final Long workflowId;
    private final String requesterEmail;

    public WorkflowApprovedEvent(Long workflowId, String requesterEmail) {
        this.workflowId = workflowId;
        this.requesterEmail = requesterEmail;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }
}