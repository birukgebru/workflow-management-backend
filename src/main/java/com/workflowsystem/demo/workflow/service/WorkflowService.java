package com.workflowsystem.demo.workflow.service;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;

public interface WorkflowService {
    
    WorkflowResponse submitRequest(
        WorkflowSubmitRequest request,
        User currentUser
    );
}
