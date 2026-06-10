package com.workflowsystem.demo.workflow.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import com.workflowsystem.demo.workflow.dto.WorkflowDashboardResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;

public interface WorkflowService {
    WorkflowResponse submitRequest(WorkflowSubmitRequest request);
    List<WorkflowResponse> getMyRequests();
    Page<WorkflowResponse> getRequestsByStatus(WorkflowStatus status, int page, int size); 
    Page<WorkflowResponse> getRequestsByTitle(String keyword, int page, int size);
    Page<WorkflowResponse> getRequestsByTitleOrDescription(String keyword, int page, int size);
    Page<WorkflowResponse> getRequestsByReviewer(Long reviewerId, int page, int size);
    Page<WorkflowResponse> getRequestsByApprover(Long approverId, int page, int size);
    Page<WorkflowResponse> getRequestsBySubmitter(Long submittedById, int page, int size); 
    Page<WorkflowResponse> getAllWorkflowRequests(int page, int size);
    WorkflowResponse getRequestById(Long id); 
    WorkflowResponse reviewRequest(@NonNull Long requestId);
    WorkflowResponse approveRequest(@NonNull Long requestId);
    WorkflowResponse rejectRequest(@NonNull Long requestId);
    WorkflowResponse assignReviewer(Long requestId, Long reviewerId);
    WorkflowResponse assignApprover(Long requestId, Long approverId); 
    WorkflowDashboardResponse getDashboardInfo();
}
