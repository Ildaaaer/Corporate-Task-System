package com.example.events.user;

import java.time.Instant;

public record UserRegisteredEvent(
        Long userId,
        String username,
        String email,
        String role,
        Instant occurredAt
) {
}
