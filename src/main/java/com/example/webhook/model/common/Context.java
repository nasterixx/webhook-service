package com.example.webhook.model.common;

public record Context(
        String sourceSystem,
        String region,
        String type,
        String pkg,
        String source
) {}
