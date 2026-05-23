package com.workflowsystem.demo.workflow.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.workflow.dto.WorkflowResponse;
import com.workflowsystem.demo.workflow.dto.WorkflowSubmitRequest;
import com.workflowsystem.demo.workflow.entity.WorkflowRequest;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;
import com.workflowsystem.demo.workflow.mapper.WorkflowRequestMapper;
import com.workflowsystem.demo.workflow.repository.WorkflowRequestRepository;
import com.workflowsystem.demo.workflow.service.WorkflowService;

@Service
public class WorkflowServiceImpl implements WorkflowService {
    private final WorkflowRequestRepository workflowRequestRepository;

    public WorkflowServiceImpl(WorkflowRequestRepository workflowRequestRepository) {
        this.workflowRequestRepository = workflowRequestRepository;
    }

    @Override
    @Transactional
    public WorkflowResponse submitRequest(WorkflowSubmitRequest submitRequest, User currentUser) {
        WorkflowRequest workflowRequest = new WorkflowRequest();
        workflowRequest.setTitle(submitRequest.getTitle());
        workflowRequest.setDescription(submitRequest.getDescription());
        workflowRequest.setSubmittedBy(currentUser);
        workflowRequest.setStatus(WorkflowStatus.PENDING);
        WorkflowRequest savedWorkflowRequest = workflowRequestRepository.save(workflowRequest);
        return WorkflowRequestMapper.toWorkflowResponse(savedWorkflowRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getMyRequests(User currentUser){
        return workflowRequestRepository.findBySubmittedByOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(WorkflowRequestMapper::toWorkflowResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getRequestsByStatus(WorkflowStatus status){
        return workflowRequestRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(WorkflowRequestMapper::toWorkflowResponse)
                .toList();
    }
}
