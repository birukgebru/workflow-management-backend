package com.workflowsystem.demo.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.enums.Role;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    long countByRoles_Name(Role roleName);
}
