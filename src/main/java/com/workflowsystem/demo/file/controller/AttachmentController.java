package com.workflowsystem.demo.file.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.workflowsystem.demo.file.dto.AttachmentResponse;
import com.workflowsystem.demo.file.entity.Attachment;
import com.workflowsystem.demo.file.repository.AttachmentRepository;
import com.workflowsystem.demo.file.service.FileStorageService;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;
import com.workflowsystem.demo.workflow.repository.WorkflowRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/workflow")
@SecurityRequirement(name = "bearerAuth")
@Tag(
    name = "Workflow Attachments Management",
    description = "Endpoints for managing workflow attachments"
)
public class AttachmentController {

    private final FileStorageService fileStorageService;
    private final WorkflowRequestRepository workflowRequestRepository;
    private final AttachmentRepository attachmentRepository;

    public AttachmentController(
            FileStorageService fileStorageService,
            WorkflowRequestRepository workflowRequestRepository,
            AttachmentRepository attachmentRepository) {

        this.fileStorageService = fileStorageService;
        this.workflowRequestRepository = workflowRequestRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @PostMapping(value = "/{workflowId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Upload attachment",
        description = "Upload an attachment to a workflow request"
    )
    public ApiResponse<AttachmentResponse> uploadAttachment(@NonNull @PathVariable Long workflowId, @RequestParam("file") MultipartFile file) {

        WorkflowRequest workflowRequest = workflowRequestRepository.findById(workflowId)
                                            .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));

        Attachment attachment = fileStorageService.store(file, workflowRequest);

        String downloadUrl = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/workflow/attachments/")
                        .pathSegment(attachment.getId().toString())
                        .toUriString();

        return new ApiResponse<>(
                true,
                "Attachment uploaded successfully",
                AttachmentResponse.fromAttachment(
                        attachment,
                        downloadUrl
                )
        );
    }

    @GetMapping("/attachments/{attachmentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Download attachment",
        description = "Download an attachment by id"
    )
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {

        Attachment attachment = fileStorageService.findById(attachmentId);
        Resource resource = fileStorageService.load(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + UriUtils.encode(
                                        attachment.getFileName(),
                                        StandardCharsets.UTF_8
                                ) + "\""
                )
                .body(resource);
    }

    //TODO: make sure appropriate role is accessing the file. also do the same for uploading attachments 
    @GetMapping("/{workflowId}/attachments")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "List workflow attachments",
        description = "Get all attachments for a workflow request"
    )
    public ApiResponse<List<AttachmentResponse>> getAttachments(@PathVariable Long workflowId) {

        WorkflowRequest workflowRequest = workflowRequestRepository.findById(workflowId)
                        .orElseThrow(() -> new ResourceNotFoundException("Workflow request not found"));
        List<AttachmentResponse> attachments = attachmentRepository
                        .findByWorkflowRequestId(workflowRequest.getId())
                        .stream()
                        .map(attachment -> {
                            String downloadUrl = ServletUriComponentsBuilder
                                            .fromCurrentContextPath()
                                            .path("/workflow/attachments/")
                                            .path(attachment.getId().toString())
                                            .toUriString();

                            return AttachmentResponse.fromAttachment(attachment, downloadUrl);
                        })
                        .toList();

        return new ApiResponse<>(
                true,
                "Attachments retrieved successfully",
                attachments
        );
    }
}