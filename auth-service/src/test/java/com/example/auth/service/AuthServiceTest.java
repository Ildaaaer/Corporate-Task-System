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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserService authUserService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void signUp_shouldCreateUserAndReturnTokenPair() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("  danil  ");
        request.setEmail("  DANIL@mail.com  ");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        AuthUser savedUser = buildUser(1L, "danil", "danil@mail.com", Role.EMPLOYEE);
        Instant refreshExpiration = Instant.parse("2030-01-01T00:00:00Z");

        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(authUserService.createUser(any(AuthUser.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(savedUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(savedUser)).thenReturn("refresh-token");
        when(jwtService.extractExpiration("refresh-token")).thenReturn(refreshExpiration);

        JwtAuthenticationResponse response = authService.signUp(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());

        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authUserService).createUser(userCaptor.capture());

        AuthUser userToSave = userCaptor.getValue();
        assertEquals("danil", userToSave.getUsername());
        assertEquals("danil@mail.com", userToSave.getEmail());
        assertEquals("encoded-password", userToSave.getPassword());
        assertEquals(Role.EMPLOYEE, userToSave.getRole());

        verify(refreshTokenService).saveOrReplace(savedUser, "refresh-token", refreshExpiration);
    }

    @Test
    void signIn_shouldAuthenticateAndReturnTokenPair() {
        SignInRequest request = new SignInRequest();
        request.setUsername("  danil  ");
        request.setPassword("password123");

        AuthUser user = buildUser(1L, "danil", "danil@mail.com", Role.MANAGER);
        Instant refreshExpiration = Instant.parse("2030-01-01T00:00:00Z");

        when(authUserService.getByUsername("danil")).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtService.extractExpiration("refresh-token")).thenReturn(refreshExpiration);

        JwtAuthenticationResponse response = authService.signIn(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());

        verify(authenticationManager).authenticate(any());
        verify(refreshTokenService).saveOrReplace(user, "refresh-token", refreshExpiration);
    }

    @Test
    void signIn_shouldThrowBadRequestExceptionWhenCredentialsAreInvalid() {
        SignInRequest request = new SignInRequest();
        request.setUsername("danil");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.signIn(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(authUserService, never()).getByUsername(any());
        verify(refreshTokenService, never()).saveOrReplace(any(), any(), any());
    }

    @Test
    void refresh_shouldValidateStoredTokenRotateAndReturnNewTokenPair() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("  old-refresh-token  ");

        AuthUser user = buildUser(1L, "danil", "danil@mail.com", Role.EMPLOYEE);
        RefreshToken storedToken = RefreshToken.builder()
                .id(10L)
                .token("old-refresh-token")
                .user(user)
                .expiresAt(Instant.parse("2030-01-01T00:00:00Z"))
                .revoked(false)
                .build();

        Instant newRefreshExpiration = Instant.parse("2031-01-01T00:00:00Z");

        when(refreshTokenService.getValidTokenOrThrow("old-refresh-token")).thenReturn(storedToken);
        when(jwtService.isRefreshTokenValid("old-refresh-token", user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(jwtService.extractExpiration("new-refresh-token")).thenReturn(newRefreshExpiration);

        JwtAuthenticationResponse response = authService.refresh(request);

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());

        verify(refreshTokenService).revokeByToken("old-refresh-token");
        verify(refreshTokenService).saveOrReplace(user, "new-refresh-token", newRefreshExpiration);
    }

    @Test
    void refresh_shouldThrowBadRequestExceptionWhenJwtRefreshTokenIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        AuthUser user = buildUser(1L, "danil", "danil@mail.com", Role.EMPLOYEE);
        RefreshToken storedToken = RefreshToken.builder()
                .id(10L)
                .token("refresh-token")
                .user(user)
                .expiresAt(Instant.parse("2030-01-01T00:00:00Z"))
                .revoked(false)
                .build();

        when(refreshTokenService.getValidTokenOrThrow("refresh-token")).thenReturn(storedToken);
        when(jwtService.isRefreshTokenValid("refresh-token", user)).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.refresh(request)
        );

        assertEquals("Refresh token is invalid", exception.getMessage());
        verify(refreshTokenService, never()).saveOrReplace(any(), any(), any());
    }

    @Test
    void logout_shouldRevokeRefreshToken() {
        LogoutRequest request = new LogoutRequest();
        request.setRefreshToken("  refresh-token  ");

        authService.logout(request);

        verify(refreshTokenService).revokeByToken("refresh-token");
    }

    @Test
    void signUp_shouldThrowBadRequestExceptionWhenPasswordsDoNotMatch() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("danil");
        request.setEmail("danil@mail.com");
        request.setPassword("password123");
        request.setConfirmPassword("password456");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.signUp(request)
        );

        assertEquals("Passwords do not match", exception.getMessage());
        verify(authUserService, never()).createUser(any());
    }

    private AuthUser buildUser(Long id, String username, String email, Role role) {
        return AuthUser.builder()
                .id(id)
                .username(username)
                .email(email)
                .password("encoded-password")
                .role(role)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }
}
