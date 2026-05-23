package com.workflowsystem.demo.workflow.service;

import java.util.List;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;

public interface WorkflowService {
    
    WorkflowResponse submitRequest(
        WorkflowSubmitRequest request,
        User currentUser
    );

    List<WorkflowResponse> getMyRequests(User currentUser);
    List<WorkflowResponse> getRequestsByStatus(WorkflowStatus status);
}
