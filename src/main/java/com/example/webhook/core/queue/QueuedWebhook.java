package com.example.webhook.core.queue;

import com.example.webhook.model.common.WebhookRequestV1;

public record QueuedWebhook(
        String traceId,
        String callbackUrl,
        WebhookRequestV1 request
) {}
