package com.workflowsystem.demo.file.repository;

<<<<<<< HEAD
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workflowsystem.demo.file.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findByWorkflowRequestId(Long workflowId);
=======
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.file.entity.Attachment;
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByWorkflowRequestId(Long workflowId);
>>>>>>> staging/main
}
