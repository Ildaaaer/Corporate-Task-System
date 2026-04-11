package com.example.events.task;

import java.time.Instant;

public record TaskCreatedEvent(
        Long taskId,
        Long authorId,
        Long assigneeId,
        String title,
        String priority,
        Instant occurredAt
) {
}
