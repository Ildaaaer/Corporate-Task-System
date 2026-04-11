package com.example.auth.service;

import com.example.auth.dto.JwtAuthenticationResponse;
import com.example.auth.dto.LogoutRequest;
import com.example.auth.dto.RefreshTokenRequest;
import com.example.auth.dto.SignInRequest;
import com.example.auth.dto.SignUpRequest;
import com.example.auth.entity.AuthUser;
import com.example.auth.entity.RefreshToken;
import com.example.auth.entity.Role;
import com.example.auth.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthUserService authUserService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        String username = normalizeUsername(request.getUsername());
        String email = normalizeEmail(request.getEmail());

        validateSignUpRequest(request, username, email);

        AuthUser userToCreate = AuthUser.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .build();

        AuthUser savedUser = authUserService.createUser(userToCreate);
        return issueTokenPair(savedUser);
    }

    @Transactional
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        String username = normalizeUsername(request.getUsername());

        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username must not be blank");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new BadRequestException("Invalid username or password");
        }

        AuthUser user = authUserService.getByUsername(username);
        return issueTokenPair(user);
    }

    @Transactional
    public JwtAuthenticationResponse refresh(RefreshTokenRequest request) {
        String rawRefreshToken = normalizeToken(request.getRefreshToken());

        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new BadRequestException("Refresh token must not be blank");
        }

        RefreshToken storedRefreshToken = refreshTokenService.getValidTokenOrThrow(rawRefreshToken);
        AuthUser user = storedRefreshToken.getUser();

        if (!jwtService.isRefreshTokenValid(rawRefreshToken, user)) {
            throw new BadRequestException("Refresh token is invalid");
        }

        refreshTokenService.revokeByToken(rawRefreshToken);
        return issueTokenPair(user);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        String rawRefreshToken = normalizeToken(request.getRefreshToken());

        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new BadRequestException("Refresh token must not be blank");
        }

        refreshTokenService.revokeByToken(rawRefreshToken);
    }

    private JwtAuthenticationResponse issueTokenPair(AuthUser user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Instant refreshTokenExpiresAt = jwtService.extractExpiration(refreshToken);

        refreshTokenService.saveOrReplace(user, refreshToken, refreshTokenExpiresAt);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    private void validateSignUpRequest(SignUpRequest request, String username, String email) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username must not be blank");
        }

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email must not be blank");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeToken(String token) {
        return token == null ? null : token.trim();
    }
}
