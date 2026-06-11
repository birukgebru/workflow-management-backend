package com.workflowsystem.demo.file.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.workflowsystem.demo.file.entity.Attachment;
import com.workflowsystem.demo.file.repository.AttachmentRepository;
import com.workflowsystem.demo.file.service.FileStorageService;
<<<<<<< HEAD
=======
import com.workflowsystem.demo.shared.exception.FileHandlingException;
>>>>>>> staging/main
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final List<String> ALLOWED_TYPES = List.of("application/pdf", "image/png", "image/jpeg");
    private final AttachmentRepository attachmentRepository;

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootLocation;

    public FileStorageServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
        } catch (IOException ex) {
<<<<<<< HEAD
            throw new RuntimeException("Could not create upload directory", ex);
=======
            throw new FileHandlingException("Could not create upload directory");
>>>>>>> staging/main
        }
    }

    @Override
    @Transactional
    public Attachment store(MultipartFile file, WorkflowRequest workflowRequest) {

        validateFile(file);
        validateWorkflowRequest(workflowRequest);

        String originalFileName = cleanOriginalFileName(file);
        String contentType = validateContentType(file.getContentType());
        String storedFileName = buildStoredFileName(originalFileName);
        Path targetLocation = rootLocation.resolve(storedFileName).normalize();

        if (!targetLocation.startsWith(rootLocation)) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Invalid file path");
=======
            throw new FileHandlingException("Invalid file path");
>>>>>>> staging/main
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
<<<<<<< HEAD
            throw new RuntimeException("Could not store file " + originalFileName, ex);
=======
            throw new FileHandlingException("Could not store file " + originalFileName);
>>>>>>> staging/main
        }

        Attachment attachment = new Attachment(
                originalFileName,
                contentType,
                file.getSize(),
                storedFileName,
                workflowRequest);

        return attachmentRepository.save(attachment);
    }

    @Override
    public Resource load(Long attachmentId) {
        Attachment attachment = findById(attachmentId);
        Path filePath = rootLocation.resolve(attachment.getFilePath()).normalize();

        if (!filePath.startsWith(rootLocation)) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Invalid file path");
=======
            throw new FileHandlingException("Invalid file path");
>>>>>>> staging/main
        }

        try {
            URI fileUri = Objects.requireNonNull(filePath.toUri(), "File URI must not be null");
            Resource resource = new UrlResource(fileUri);

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new ResourceNotFoundException("File not found");
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found");
        }
    }

    @Override
    public Attachment findById(Long attachmentId) {
        if (attachmentId == null) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Attachment id is required");
=======
            throw new FileHandlingException("Attachment id is required");
>>>>>>> staging/main
        }

        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
<<<<<<< HEAD
            throw new IllegalArgumentException("File is required");
=======
            throw new FileHandlingException("File is required");
>>>>>>> staging/main
        }

        String fileName = cleanOriginalFileName(file);

        if (fileName.contains("..")) {
<<<<<<< HEAD
            throw new IllegalArgumentException("File name contains invalid path sequence");
        }

        if (file.getSize() > maxFileSize.toBytes()) {
            throw new IllegalArgumentException("File size must not exceed 10MB");
=======
            throw new FileHandlingException("File name contains invalid path sequence");
        }

        if (file.getSize() > maxFileSize.toBytes()) {
            throw new FileHandlingException("File size must not exceed 10MB");
>>>>>>> staging/main
        }

        validateContentType(file.getContentType());
    }

    private void validateWorkflowRequest(WorkflowRequest workflowRequest) {
        if (workflowRequest == null || workflowRequest.getId() == null) {
            throw new ResourceNotFoundException("Workflow request is required");
        }
    }

    private String cleanOriginalFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFileName)) {
<<<<<<< HEAD
            throw new IllegalArgumentException("File name is required");
=======
            throw new FileHandlingException("File name is required");
>>>>>>> staging/main
        }

        return StringUtils.cleanPath(originalFileName);
    }

    private String validateContentType(String contentType) {
        if (!StringUtils.hasText(contentType) || !ALLOWED_TYPES.contains(contentType)) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Only PDF, PNG, and JPEG files are allowed");
=======
            throw new FileHandlingException("Only PDF, PNG, and JPEG files are allowed");
>>>>>>> staging/main
        }

        return contentType;
    }

    private String buildStoredFileName(String originalFileName) {
        String extension = "";
        int extensionIndex = originalFileName.lastIndexOf('.');

        if (extensionIndex >= 0) {
            extension = originalFileName.substring(extensionIndex);
        }

        return UUID.randomUUID() + extension;
    }
}
