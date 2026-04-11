package com.example.auth.service;

import com.example.auth.dto.JwtAuthenticationResponse;
import com.example.auth.dto.SignUpRequest;
import com.example.auth.entity.Role;
import com.example.auth.entity.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserService authUserService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void signUp_shouldCreateUserAndReturnToken() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("  danil  ");
        request.setEmail("  DANIL@mail.com  ");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        AuthUser savedUser = AuthUser.builder()
                .id(1L)
                .username("danil")
                .email("danil@mail.com")
                .password("encoded-password")
                .role(Role.EMPLOYEE)
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(authUserService.createUser(any(AuthUser.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");

        JwtAuthenticationResponse response = authService.signUp(request);

        assertEquals("jwt-token", response.getToken());

        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authUserService).createUser(userCaptor.capture());

        AuthUser userToSave = userCaptor.getValue();
        assertEquals("danil", userToSave.getUsername());
        assertEquals("danil@mail.com", userToSave.getEmail());
        assertEquals("encoded-password", userToSave.getPassword());
        assertEquals(Role.EMPLOYEE, userToSave.getRole());
    }


}