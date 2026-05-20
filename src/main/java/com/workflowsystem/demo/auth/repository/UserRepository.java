package com.workflowsystem.demo.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.auth.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
}
