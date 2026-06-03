package com.workflowsystem.demo.auth.service;

import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.auth.repository.RoleRepository;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserResponse assignRole(Long userId, Role roleName) {

        User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

        com.workflowsystem.demo.auth.entity.Role role =
                roleRepository.findByName(roleName)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Role not found"));

        user.getRoles().clear();
        user.getRoles().add(role);
        User savedUser = userRepository.save(user);

        return UserMapper.toUserResponse(savedUser);
    }
}
