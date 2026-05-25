package com.workflowsystem.demo.shared.exception;

public class InvalidWorkflowStateException extends RuntimeException {
    public InvalidWorkflowStateException(String message) {
        super(message);
    }
    
}
