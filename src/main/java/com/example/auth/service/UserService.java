package com.example.auth.service;


import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.exceptions.BadRequestException;
import com.example.auth.exceptions.NotFoundException;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(User user) {
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
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ADMIN);
        save(user);
    }



}