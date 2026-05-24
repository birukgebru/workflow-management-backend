package com.workflowsystem.demo.workflow.service;

import java.util.List;

import org.springframework.lang.NonNull;

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
    WorkflowResponse reviewRequest(@NonNull Long requestId, User currentUser);
    WorkflowResponse approveRequest(@NonNull Long requestId, User currentUser);
    WorkflowResponse rejectRequest(@NonNull Long requestId, User currentUser);
}
