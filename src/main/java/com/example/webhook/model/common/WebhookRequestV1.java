package com.example.webhook.model.common;

import java.util.Map;

public record WebhookRequestV1(
        Data data
) {
    public record Data(
            String type,
            Attributes attributes
    ) {}

    public record Attributes(
            String id,
            String name,
            String occuredOn,
            String contentType,
            String contentSchema,
            AdditionalContext additonalContext,
            Map<String, Object> payload
    ) {}
}
