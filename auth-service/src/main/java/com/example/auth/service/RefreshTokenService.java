package com.example.auth.service;

import com.example.auth.entity.AuthUser;
import com.example.auth.entity.RefreshToken;
import com.example.auth.exceptions.BadRequestException;
import com.example.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    @Transactional
    public RefreshToken saveOrReplace(AuthUser user, String token, Instant expiresAt) {
        validateSaveArguments(user, token, expiresAt);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(RefreshToken::new);

        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getValidTokenOrThrow(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Refresh token is invalid"));

        assertUsable(refreshToken);
        return refreshToken;
    }

    @Transactional
    public void revokeByToken(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        refreshTokenRepository.findByToken(token)
                .ifPresent(this::markRevoked);
    }

    @Transactional
    public void revokeByUserId(Long userId) {
        if (userId == null) {
            return;
        }

        refreshTokenRepository.findByUserId(userId)
                .ifPresent(this::markRevoked);
    }

    public void assertUsable(RefreshToken refreshToken) {
        if (refreshToken.isRevoked()) {
            throw new BadRequestException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now(clock))) {
            throw new BadRequestException("Refresh token has expired");
        }
    }

    private void markRevoked(RefreshToken refreshToken) {
        if (refreshToken.isRevoked()) {
            return;
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private void validateSaveArguments(AuthUser user, String token, Instant expiresAt) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User must be persisted before storing a refresh token");
        }

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Refresh token must not be blank");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("Refresh token expiration must not be null");
        }

        if (!expiresAt.isAfter(Instant.now(clock))) {
            throw new IllegalArgumentException("Refresh token expiration must be in the future");
        }
    }
}
