package com.workflowsystem.demo.workflow.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.workflow.entity.WorkflowComment;

public interface WorkflowCommentRepository extends JpaRepository<WorkflowComment, Long> {
    List<WorkflowComment> findByWorkflowRequestIdOrderByIdAsc(Long workflowRequestId);
}
