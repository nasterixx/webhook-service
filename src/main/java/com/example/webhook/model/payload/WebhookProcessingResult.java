package com.example.webhook.model.payload;

public record WebhookProcessingResult(
        String ns3Url,
        String moduleCStatus
) {}
