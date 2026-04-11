package com.example.events.task;

import java.time.Instant;

public record TaskStatusChangedEvent(
        Long taskId,
        String oldStatus,
        String newStatus,
        Long changedBy,
        Instant occurredAt
) {
}
