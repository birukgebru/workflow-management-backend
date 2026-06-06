package com.workflowsystem.demo.workflow.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.workflow.dto.WorkflowDashboardResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;

public interface WorkflowService {
    WorkflowResponse submitRequest(WorkflowSubmitRequest request, User currentUser );
    List<WorkflowResponse> getMyRequests(User currentUser);
    List<WorkflowResponse> getRequestsByStatus(WorkflowStatus status);
    Page<WorkflowResponse> getAllWorkflowRequests(int page, int size);
    WorkflowResponse getRequestById(Long id); 
    WorkflowResponse reviewRequest(@NonNull Long requestId, User currentUser);
    WorkflowResponse approveRequest(@NonNull Long requestId, User currentUser);
    WorkflowResponse rejectRequest(@NonNull Long requestId, User currentUser);
    WorkflowResponse assignReviewer(Long requestId, Long reviewerId, User currentUser);
    WorkflowResponse assignApprover(Long requestId, Long approverId, User currentUser); 
    WorkflowDashboardResponse getDashboardInfo(User currentUser);
}
