package com.workflowsystem.demo.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.workflowsystem.demo.auth.entity.RefreshToken;
import com.workflowsystem.demo.auth.entity.User;
import com.workflowsystem.demo.auth.repository.RefreshTokenRepository;


@Service
public class RefreshTokenService {
    
    @Value("${refresh-token.expiration}")
    private long refreshTokenDuration;

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken (User user) {
        // RefreshToken refreshToken = new RefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDuration));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isExpired(RefreshToken token){
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    public Optional<RefreshToken> findByToken(String token) {

        return refreshTokenRepository.findByToken(token);
    }

}
