package com.example.webhook.model.common;

public record WebhookRequestV1<T>(Data<T> data) {

    public record Data<T>(String type, Attributes<T> attributes) {}

    public record Attributes<T>(
            String id,
            String name,
            String occuredOn,
            String contentType,
            String contentSchema,
            AdditionalContext additonalContext,
            T payload
    ) {}
}
