package com.workflowsystem.demo.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.dto.ResetPasswordRequest;
import com.workflowsystem.demo.auth.entity.PasswordResetToken;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.PasswordResetTokenRepository;
import com.workflowsystem.demo.auth.repository.UserRepository;
import com.workflowsystem.demo.shared.exception.InvalidTokenException;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    public PasswordResetService(
        PasswordResetTokenRepository passwordResetTokenRepository, 
        UserRepository userRepository, 
        PasswordEncoder passwordEncoder
    ) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateResetToken(String email) {
        logger.info("Password reset requested for email: {}",email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->{
                    logger.warn("Password reset requested for unknown email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        PasswordResetToken psdResetToken = passwordResetTokenRepository.findByUser(user)
                .orElseGet(PasswordResetToken::new);

        psdResetToken.setToken(generateUniqueToken());
        psdResetToken.setUser(user);
        psdResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        psdResetToken.setUsed(false);
        passwordResetTokenRepository.save(psdResetToken);
    
        logger.info("Password reset token generated for email: {}", email);

        return psdResetToken.getToken();
    }

    public void resetPassword(ResetPasswordRequest request) {
        logger.info("Password reset attempt using token");
        
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                        .orElseThrow(() -> new ResourceNotFoundException("Invalid password reset token"));
        validateResetToken(token);
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        token.setUsed(true);

        passwordResetTokenRepository.save(token);
    }

    private void validateResetToken(PasswordResetToken token) {
        if (token.isUsed()) {
            throw new InvalidTokenException();
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException();
        }
    }

    private String generateUniqueToken() {
        String token;

        do {
            token = UUID.randomUUID().toString();
        } while (passwordResetTokenRepository.existsByToken(token));

        return token;
    }
}
