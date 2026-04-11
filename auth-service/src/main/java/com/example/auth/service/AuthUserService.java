package com.example.auth.service;


import com.example.auth.entity.AuthUser;
import com.example.auth.exceptions.BadRequestException;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthUserService {
    private final UserRepository userRepository;

    @Transactional
    public AuthUser save(AuthUser user) {
        return userRepository.save(user);
    }

    @Transactional
    public AuthUser createUser(AuthUser user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(existingUser -> {
                    throw new BadRequestException("User with username already exists");
                });

        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new BadRequestException("User with email already exists");
                });
        return save(user);
    }
    @Transactional(readOnly = true)
    public AuthUser getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public AuthUser getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }




}