package com.workflowsystem.demo.auth.service;

import com.workflowsystem.demo.audit.entitiy.AuditLog;
import com.workflowsystem.demo.audit.repository.AuditLogRepository;
import com.workflowsystem.demo.audit.service.AuditLogService;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.dto.UserResponse;
import com.workflowsystem.demo.auth.enums.Role;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.mapper.UserMapper;
import com.workflowsystem.demo.auth.repository.RoleRepository;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.AuthenticationException;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository, AuditLogService auditLogService, AuditLogRepository auditLogRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditLogService = auditLogService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public UserResponse assignRole(Long userId, Role roleName, User currentUser) {

        User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

        if (user.getRoles().stream()
                .anyMatch(r -> r.getName() == Role.ROLE_ADMIN)
                && roleName != Role.ROLE_ADMIN) {

            throw new AuthenticationException("Admin cannot be demoted");
        }
        
        com.workflowsystem.demo.auth.entity.Role role =
                roleRepository.findByName(roleName)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Role not found"));
        
        user.getRoles().clear();
        user.getRoles().add(role);
        User savedUser = userRepository.save(user);

        logAudit(
                "ROLE_ASSIGNED",
                "User",
                user.getId(),
                currentUser,
                "Assigned role " + roleName
        );

        return UserMapper.toUserResponse(savedUser);
    }



    private void logAudit(
            String action,
            String entityType,
            Long entityId,
            User performedBy,
            String details
    ) {
        AuditLog auditLog = new AuditLog(
                action,
                entityType,
                entityId,
                performedBy,
                details
        );
        auditLogRepository.save(auditLog);
    }

}
