package com.workflowsystem.demo.workflow.dto;

public class WorkflowDashboardResponse {
    private Long totalSubmitted;
    private Long totalPending;
    private Long totalUnderReview;
    private Long totalApproved;
    private Long totalRejected;

    public WorkflowDashboardResponse() {}

    public WorkflowDashboardResponse(Long totalSubmitted, Long totalPending, Long totalUnderReview, Long totalApproved, Long totalRejected) {
        
        this.totalSubmitted = totalSubmitted;
        this.totalPending = totalPending;
        this.totalUnderReview = totalUnderReview;
        this.totalApproved = totalApproved;
        this.totalRejected = totalRejected;
    }

    public Long getTotalApproved() {
        return totalApproved;
    }

    public Long getTotalRejected() {
        return totalRejected;
    }

    public Long getTotalPending() {
        return totalPending;
    }

    public Long getTotalUnderReview(){
        return totalUnderReview;
    }

    public Long getTotalSubmitted() {
        return totalSubmitted;
    }
}
