package com.workflowsystem.demo.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WorkflowSubmitRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    private String description;
    
    public WorkflowSubmitRequest(){}

    public void setTitle(String title){
        this.title = title;
    }
    
    public String getTitle(){
        return title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}