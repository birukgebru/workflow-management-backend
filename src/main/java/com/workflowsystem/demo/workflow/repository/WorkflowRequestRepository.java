package com.workflowsystem.demo.workflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workflowsystem.demo.workflow.entity.WorkflowRequest;
import com.workflowsystem.demo.auth.entity.User;


public interface WorkflowRequestRepository extends JpaRepository<WorkflowRequest, Long>{
    List<WorkflowRequest> findBySubmittedBy(User submittedBy); 
    
}
