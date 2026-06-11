package com.workflowsystem.demo.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.workflowsystem.demo.auth.entity.PasswordResetToken;
import com.workflowsystem.demo.auth.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
   Optional<PasswordResetToken> findByToken(String token);
   Optional<PasswordResetToken> findByUser(User user);
   boolean existsByToken(String token);
    
}
