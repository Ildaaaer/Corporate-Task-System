package com.example.auth.controller;

import com.example.auth.dto.AccountStatusResponse;
import com.example.auth.dto.ChangeRoleRequest;
import com.example.auth.entity.AuthUser;
import com.example.auth.mapper.AccountStatusMapper;
import com.example.auth.service.AuthUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuthController {

    private final AuthUserService authUserService;
    private final AccountStatusMapper accountStatusMapper;

    @PatchMapping("/{userId}/block")
    @ResponseStatus(HttpStatus.OK)
    public AccountStatusResponse blockUser(@PathVariable Long userId) {
        AuthUser user = authUserService.blockUser(userId);
        return accountStatusMapper.toResponse(user);
    }

    @PatchMapping("/{userId}/unblock")
    @ResponseStatus(HttpStatus.OK)
    public AccountStatusResponse unblockUser(@PathVariable Long userId) {
        AuthUser user = authUserService.unblockUser(userId);
        return accountStatusMapper.toResponse(user);
    }

    @PatchMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public AccountStatusResponse deactivateUser(@PathVariable Long userId) {
        AuthUser user = authUserService.deactivateUser(userId);
        return accountStatusMapper.toResponse(user);
    }

    @PatchMapping("/{userId}/activate")
    @ResponseStatus(HttpStatus.OK)
    public AccountStatusResponse activateUser(@PathVariable Long userId) {
        AuthUser user = authUserService.activateUser(userId);
        return accountStatusMapper.toResponse(user);
    }

    @PatchMapping("/{userId}/role")
    @ResponseStatus(HttpStatus.OK)
    public AccountStatusResponse changeRole(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeRoleRequest request
    ) {
        AuthUser user = authUserService.changeRole(userId, request.getRole());
        return accountStatusMapper.toResponse(user);
    }


}
