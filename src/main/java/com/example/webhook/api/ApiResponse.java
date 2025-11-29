package com.example.webhook.api;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        String status,
        String message,
        String requestId,
        String traceId,
        long durationMs,
        T result
) {}
