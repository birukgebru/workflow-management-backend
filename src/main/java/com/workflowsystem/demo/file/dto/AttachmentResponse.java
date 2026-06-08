package com.workflowsystem.demo.file.dto;

import java.time.LocalDateTime;

import com.workflowsystem.demo.file.entity.Attachment;

public class AttachmentResponse {
    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private Long workflowRequestId;
    private String downloadUrl;
    private LocalDateTime createdAt;

    public AttachmentResponse() {}

    public AttachmentResponse(
            Long id,
            String fileName,
            String contentType,
            Long fileSize,
            Long workflowRequestId,
            String downloadUrl,
            LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.workflowRequestId = workflowRequestId;
        this.downloadUrl = downloadUrl;
        this.createdAt = createdAt;
    }

    public static AttachmentResponse fromAttachment(Attachment attachment, String downloadUrl) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getWorkflowRequest().getId(),
                downloadUrl,
                attachment.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getWorkflowRequestId() {
        return workflowRequestId;
    }

    public void setWorkflowRequestId(Long workflowRequestId) {
        this.workflowRequestId = workflowRequestId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
