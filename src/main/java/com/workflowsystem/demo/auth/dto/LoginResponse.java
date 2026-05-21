package com.workflowsystem.demo.auth.dto;

public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;

    public LoginResponse() {}

    public LoginResponse(Long id, String username, String email, String accessToken, String refreshToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmail() {
        return email;
    }

    public String getRefreshToken(){
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
