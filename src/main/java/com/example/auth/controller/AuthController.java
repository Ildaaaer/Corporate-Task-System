package com.example.auth.controller;

import com.example.auth.dto.JwtAuthenticationResponse;
import com.example.auth.dto.SignInRequest;
import com.example.auth.dto.SignUpRequest;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authService.signUp(signUpRequest);

    }


    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return authService.signIn(signInRequest);
    }

}