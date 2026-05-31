package com.workflowsystem.demo.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.entity.PasswordResetToken;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.PasswordResetTokenRepository;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    
    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
    }

    public String generateResetToken(String email) {
        PasswordResetToken psdResetToken = new PasswordResetToken();
        psdResetToken.setToken(UUID.randomUUID().toString());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        psdResetToken.setUser(user);
        psdResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        psdResetToken.setUsed(false);
        passwordResetTokenRepository.save(psdResetToken);

        return psdResetToken.getToken();
    }
}
