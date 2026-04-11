package com.example.auth.service;

import com.example.auth.entity.AuthUser;
import com.example.auth.entity.Role;
import com.example.auth.exceptions.BadRequestException;
import com.example.auth.exceptions.NotFoundException;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUserService {

    private final UserRepository userRepository;

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

        return userRepository.save(user);
    }

    public AuthUser getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id %d not found".formatted(userId)));
    }

    public AuthUser getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username '%s' not found".formatted(username)));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public AuthUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BadRequestException("No authenticated user in security context");
        }

        return getByUsername(authentication.getName());
    }


    @Transactional
    public AuthUser changeRole(Long userId, Role newRole) {
        if (newRole == null) {
            throw new BadRequestException("Role must not be null");
        }

        AuthUser user = getById(userId);

        if (user.getRole() == newRole) {
            return user;
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }
    @Transactional
    public AuthUser blockUser(Long userId) {
        AuthUser user = getById(userId);

        if (!user.isAccountNonLocked()) {
            return user;
        }

        user.setAccountNonLocked(false);
        return userRepository.save(user);
    }

    @Transactional
    public AuthUser unblockUser(Long userId) {
        AuthUser user = getById(userId);

        if (user.isAccountNonLocked()) {
            return user;
        }

        user.setAccountNonLocked(true);
        return userRepository.save(user);
    }

    @Transactional
    public AuthUser deactivateUser(Long userId) {
        AuthUser user = getById(userId);

        if (!user.isEnabled()) {
            return user;
        }

        user.setEnabled(false);
        return userRepository.save(user);
    }

    @Transactional
    public AuthUser activateUser(Long userId) {
        AuthUser user = getById(userId);

        if (user.isEnabled()) {
            return user;
        }

        user.setEnabled(true);
        return userRepository.save(user);
    }
}
