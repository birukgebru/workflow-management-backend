package com.workflowsystem.demo.file.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.file.entity.Attachment;
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByWorkflowRequestId(Long workflowId);
}
