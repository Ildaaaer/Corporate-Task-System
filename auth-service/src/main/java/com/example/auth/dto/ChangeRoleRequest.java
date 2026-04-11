package com.example.auth.dto;

import com.example.auth.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequest {

    @NotNull
    private Role role;
}