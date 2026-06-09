package com.workflowsystem.demo.notification.event;

public class ReviewerAssignedEvent {

    private final Long workflowId;
    private final String reviewerEmail;

    public ReviewerAssignedEvent(Long workflowId, String reviewerEmail) {
        this.workflowId = workflowId;
        this.reviewerEmail = reviewerEmail;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }
}