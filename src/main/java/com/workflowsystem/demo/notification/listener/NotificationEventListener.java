package com.workflowsystem.demo.notification.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.workflowsystem.demo.notification.event.ApproverAssignedEvent;
import com.workflowsystem.demo.notification.event.ReviewerAssignedEvent;
import com.workflowsystem.demo.notification.event.WorkflowApprovedEvent;
import com.workflowsystem.demo.notification.event.WorkflowRejectedEvent;
import com.workflowsystem.demo.notification.event.WorkflowSubmittedEvent;
import com.workflowsystem.demo.notification.service.NotificationService;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("notificationExecutor")
    @EventListener
    public void handleWorkflowSubmitted(WorkflowSubmittedEvent event) {
        notificationService.sendEmail(
                event.getRequesterEmail(),
                "Workflow Submitted",
                "Workflow #" + event.getWorkflowId() + " has been submitted successfully."
        );
    }

    @Async("notificationExecutor")
    @EventListener
    public void handleWorkflowApproved(WorkflowApprovedEvent event) {

        notificationService.sendEmail(
                event.getRequesterEmail(),
                "Workflow Approved",
                "Workflow #" + event.getWorkflowId() + " has been approved."
        );
    }

    @Async("notificationExecutor")
    @EventListener
    public void handleWorkflowRejected(WorkflowRejectedEvent event) {

        notificationService.sendEmail(
                event.getRequesterEmail(),
                "Workflow Rejected",
                "Workflow #" + event.getWorkflowId() + " has been rejected."
        );
    }

    @Async("notificationExecutor")
    @EventListener
    public void handleReviewerAssigned(ReviewerAssignedEvent event) {

        notificationService.sendEmail(
                event.getReviewerEmail(),
                "Workflow Assigned For Review",
                "Workflow #" + event.getWorkflowId() + " has been assigned to you."
        );
    }

    @Async("notificationExecutor")
    @EventListener
    public void handleApproverAssigned(ApproverAssignedEvent event) {

        notificationService.sendEmail(
                event.getApproverEmail(),
                "Workflow Assigned For Approval",
                "Workflow #" + event.getWorkflowId() + " has been assigned to you."
        );
    }
}