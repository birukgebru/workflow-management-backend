package com.workflowsystem.demo.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.entity.RefreshToken;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.RefreshTokenRepository;
import com.workflowsystem.demo.shared.exception.ResourceNotFoundException;


@Service
public class RefreshTokenService {
    
    @Value("${refresh-token.expiration}")
    private long refreshTokenDuration;

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken (User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDuration));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new ResourceNotFoundException("Refresh token revoked");
        }

        if (isExpired(refreshToken)) {
            throw new ResourceNotFoundException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revokeToken(String token) {

            RefreshToken refreshToken = refreshTokenRepository
                    .findByToken(token)
                    .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

            refreshToken.setRevoked(true);

            refreshTokenRepository.save(refreshToken);
    }


    public boolean isExpired(RefreshToken token){
       
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    public Optional<RefreshToken> findByToken(String token) {

        return refreshTokenRepository.findByToken(token);
    }

}
