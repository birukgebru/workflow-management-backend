package com.workflowsystem.demo.file.entity;

import java.time.LocalDateTime;

import com.workflowsystem.demo.workflow.entity.WorkflowRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 500)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_request_id", nullable = false)
    private WorkflowRequest workflowRequest;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Attachment() {}

    public Attachment(
            String fileName,
            String contentType,
            Long fileSize,
            String filePath,
            WorkflowRequest workflowRequest) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.workflowRequest = workflowRequest;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public WorkflowRequest getWorkflowRequest() {
        return workflowRequest;
    }

    public void setWorkflowRequest(WorkflowRequest workflowRequest) {
        this.workflowRequest = workflowRequest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
