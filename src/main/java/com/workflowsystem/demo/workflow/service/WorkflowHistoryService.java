package com.workflowsystem.demo.workflow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workflowsystem.demo.workflow.dto.WorkflowHistoryResponse;
import com.workflowsystem.demo.workflow.mapper.WorkflowHistoryMapper;
import com.workflowsystem.demo.workflow.repository.WorkflowHistoryRepository;

import java.util.List;
@Service
public class WorkflowHistoryService {
    private final WorkflowHistoryRepository workflowHistoryRepository;

    public WorkflowHistoryService(WorkflowHistoryRepository workflowHistoryRepository){
        this.workflowHistoryRepository = workflowHistoryRepository;
    }
    
    @Transactional
    public List<WorkflowHistoryResponse> getWorkflowHistories(Long id ){
        return workflowHistoryRepository
                        .findByWorkflowRequestIdOrderByChangedAtAsc(id)
                        .stream()
                        .map(WorkflowHistoryMapper::toWorkflowHistoryResponse)
                        .toList();
    }
        
    
}
