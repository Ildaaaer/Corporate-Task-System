package com.example.auth.mapper;

import com.example.auth.dto.AccountStatusResponse;
import com.example.auth.entity.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class AccountStatusMapper {

    public AccountStatusResponse toResponse(AuthUser user) {
        return AccountStatusResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .build();
    }
}
