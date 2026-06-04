package com.workflowsystem.demo.workflow.state;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import com.workflowsystem.demo.shared.exception.InvalidWorkflowStateException;
import com.workflowsystem.demo.workflow.enums.WorkflowStatus;

@Component
public class WorkflowStateMachine {
    private static final Map<WorkflowStatus, Set<WorkflowStatus>> ALLOWED_TRANSITIONS = Map.of(
        WorkflowStatus.PENDING, 
        Set.of(WorkflowStatus.UNDER_REVIEW),

        WorkflowStatus.UNDER_REVIEW,
        Set.of(WorkflowStatus.APPROVED, WorkflowStatus.REJECTED)
    );

    public void validateTransition(WorkflowStatus from, WorkflowStatus to) {
        boolean allowed = ALLOWED_TRANSITIONS
                .getOrDefault(from, Set.of())
                .contains(to);

        if (!allowed) {
            throw new InvalidWorkflowStateException("Cannot transition from " + from + " to "  + to);
        }
    }
}
