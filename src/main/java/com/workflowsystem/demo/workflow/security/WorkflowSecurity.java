package com.workflowsystem.demo.workflow.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.workflowsystem.demo.workflow.entity.WorkflowRequest;
import com.workflowsystem.demo.workflow.repository.WorkflowRequestRepository;

@Component
public class WorkflowSecurity {
    private final WorkflowRequestRepository workflowRequestRepository;

    public WorkflowSecurity(WorkflowRequestRepository workflowRequestRepository) {
        this.workflowRequestRepository = workflowRequestRepository;
    }

    public boolean canViewRequest (Long requestId, Authentication authentication){
        WorkflowRequest workflowRequest = workflowRequestRepository.findById(requestId).orElse(null);

        if(workflowRequest == null){
            return false;
        }

       if(isAdmin(authentication)){ 
        
            return true;
        }

        String email = authentication.getName();

        // Check if the authenticated user is the one who submitted the request
        if (workflowRequest.getSubmittedBy() != null && workflowRequest.getSubmittedBy().getEmail().equals(email)) {

            return true;
        }

        /**-----------------------------------------
        // TODO: I will implement assign reviewer and approver. 
        // Check if the authenticated user is the assigned reviewer for the request
        if (workflowRequest.getAssignedReviewer() != null && workflowRequest.getAssignedReviewer().getEmail().equals(email)) {

            return true;
        }

        // Check if the authenticated user is the assigned approver for the request
        if (workflowRequest.getAssignedApprover() != null && workflowRequest.getAssignedApprover().getEmail().equals(email)) {

            return true;
        }
        -----------------------------------------*/


        return false;
    }


    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(
                    grantedAuthority -> grantedAuthority.getAuthority()
                    .equals("ROLE_ADMIN")
                );
    }



}
