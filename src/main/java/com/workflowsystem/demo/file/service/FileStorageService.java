package com.workflowsystem.demo.file.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.workflowsystem.demo.file.entity.Attachment;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;

public interface FileStorageService {
    Attachment store(MultipartFile file, WorkflowRequest workflowRequest);
    Resource load(Long attachmentId);
    Attachment findById(Long attachmentId);
}
