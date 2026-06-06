package com.workflowsystem.demo.workflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;
import com.workflowsystem.demo.auth.entity.User;


public interface WorkflowRequestRepository extends JpaRepository<WorkflowRequest, Long>{
    List<WorkflowRequest> findBySubmittedByOrderByCreatedAtDesc(User user);
    List<WorkflowRequest> findByStatusOrderByCreatedAtDesc(WorkflowStatus status);
    Page<WorkflowRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @Query("SELECT COUNT(w) FROM WorkflowRequest w")
    Long countAll();
    Long countByStatus(WorkflowStatus status);
    
}
