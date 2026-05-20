package com.workflowsystem.demo.shared.controller;

import org.springframework.web.bind.annotation.RestController;

import com.workflowsystem.demo.auth.dto.RegisterRequest;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;
import com.workflowsystem.demo.shared.response.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class TestController {


    @GetMapping("/apires/test")
    public ApiResponse<String> test() {
        return new ApiResponse<>(true, "Success", "My firstResponse data");
    }
    
    @GetMapping("/error-test")
    public ApiResponse<Object> errorTest() {
        throw new ResourceNotFoundException("request file not found");
    }
    
    @PostMapping("/validate-test")
    public ApiResponse<Object> validateTest(@Valid @RequestBody RegisterRequest request) {  
        
        return new ApiResponse<>(
            true,
            "Validation passed",
            request
        );
    }
    
}
