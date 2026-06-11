package com.workflowsystem.demo.auth.dto;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String name;


    public UserResponse() {}

    public UserResponse(Long id, String username, String email, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
