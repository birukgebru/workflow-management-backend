package com.workflowsystem.demo.workflow.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.workflow.entity.WorkflowHistory;

public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, Long>{
    List<WorkflowHistory> findByWorkflowRequestIdOrderByChangedAtAsc(Long workflowId);
}
    
