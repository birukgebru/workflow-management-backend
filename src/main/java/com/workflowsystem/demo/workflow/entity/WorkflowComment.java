package com.workflowsystem.demo.workflow.entity;

import java.time.LocalDateTime;

import com.workflowsystem.demo.auth.entity.User;

import jakarta.persistence.*;

@Entity
@Table(name = "workflow_comment")
public class WorkflowComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_instance_id", nullable = false)    
    private WorkflowRequest workflowRequest;

    @Column(nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "commenter_id", nullable = false)
    private User commenter;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setWorkflowRequest(WorkflowRequest workflowRequest) {
        this.workflowRequest = workflowRequest;
    }

    public WorkflowRequest getWorkflowRequest() {
        return workflowRequest;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public User getCommenter() {
        return commenter;
    }

    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }
}
