package com.workflowsystem.demo.file.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public class AttachmentRequest {
    @NotNull(message = "File is required")
    private MultipartFile file;

    public AttachmentRequest() {}

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
