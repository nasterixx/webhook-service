package com.example.webhook.api;

import java.time.Instant;
import java.util.Map;

public record ApiResponse(
        Instant timestamp,
        String status,
        String message,
        String requestId,
        String traceId,
        long durationMs,
        Map<String, Object> result
) {}
