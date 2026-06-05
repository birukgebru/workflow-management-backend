package com.workflowsystem.demo.shared.exception;

public class InvalidWorkflowCommentException extends RuntimeException {
    public InvalidWorkflowCommentException(String message) {
        super(message);
    }
}
