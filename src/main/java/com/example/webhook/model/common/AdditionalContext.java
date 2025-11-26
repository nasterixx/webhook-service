package com.example.webhook.model.common;

public record AdditionalContext(
        String sourceSystem,
        String region,
        String type,
        String pkg,
        String source
) {}
