package com.example.events.task;

import java.time.Instant;

public record TaskAssignedEvent(
        Long taskId,
        Long assigneeId,
        Long assignedBy,
        Instant occurredAt
) {
}
