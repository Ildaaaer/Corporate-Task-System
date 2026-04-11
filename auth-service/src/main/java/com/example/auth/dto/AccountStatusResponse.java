package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusResponse {

    private Long userId;
    private String username;
    private String email;
    private String role;
    private boolean enabled;
    private boolean accountNonLocked;
}
