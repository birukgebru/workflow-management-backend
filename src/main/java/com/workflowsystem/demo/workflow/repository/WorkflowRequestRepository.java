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
    Page<WorkflowRequest> findByStatusOrderByCreatedAtDesc(WorkflowStatus status, Pageable pageable);
    Page<WorkflowRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<WorkflowRequest> findByAssignedReviewerId(Long reviewerId, Pageable pageable);
    Page<WorkflowRequest> findByAssignedApproverId(Long approvedBy, Pageable pageable);
    Page<WorkflowRequest> findBySubmittedById(Long submittedById, Pageable pageable);
    @Query("SELECT COUNT(w) FROM WorkflowRequest w")
    Long countAll();
    Long countByStatus(WorkflowStatus status);
    
}
