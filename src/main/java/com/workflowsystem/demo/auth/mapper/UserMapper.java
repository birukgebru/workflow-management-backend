package com.workflowsystem.demo.auth.mapper;

import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.entity.User;

public final class UserMapper {

    private UserMapper(){}

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
    
}
