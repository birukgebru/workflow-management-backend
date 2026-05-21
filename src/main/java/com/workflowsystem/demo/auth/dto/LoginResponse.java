package com.workflowsystem.demo.auth.dto;

public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private String token;

    public LoginResponse() {}

    public LoginResponse(Long id, String username, String email, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }


    public String getEmail() {
        return email;
    }
}
