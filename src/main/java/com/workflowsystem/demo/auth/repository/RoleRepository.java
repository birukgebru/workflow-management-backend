package com.workflowsystem.demo.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.auth.entity.Role;

public interface RoleRepository  extends JpaRepository<Role, Long> {
    Optional<Role> findByName(com.workflowsystem.demo.auth.enums.Role name);
}
