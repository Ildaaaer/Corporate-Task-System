package com.example.auth.service;

import com.example.auth.dto.JwtAuthenticationResponse;
import com.example.auth.dto.SignInRequest;
import com.example.auth.dto.SignUpRequest;
import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    // Регистрация пользователя
    @Transactional
    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest) {
        String username = normalizeUsername(signUpRequest.getUsername());
        String email = normalizeEmail(signUpRequest.getEmail());

        if(!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        var user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .build();

        var savedUser = userService.createUser(user);
        var jwt = jwtService.generateToken(savedUser);
        return new JwtAuthenticationResponse(jwt);
    }

    // Аутентификация пользователя
    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        String username = normalizeUsername(signInRequest.getUsername());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, signInRequest.getPassword()));

        var user = userService.userDetailsService().loadUserByUsername(username);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }



}