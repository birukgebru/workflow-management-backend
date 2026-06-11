package com.workflowsystem.demo.shared.exception;

public class InvalidAccountManagementException extends RuntimeException{
    public InvalidAccountManagementException(String message){
        super(message);
    }
}
