package com.example.webhook.model.common;

import com.example.webhook.model.payload.LabelCreatedPayload;
import com.example.webhook.model.payload.OrderCreatedPayload;
import com.example.webhook.model.payload.PurchaseCreatedPayload;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public record WebhookRequestV1<T>(Data<T> data) {

    public record Data<T>(String type, Attributes<T> attributes) {}

    public record Attributes<T>(
            String id,
            String name,
            String contentType,
            String schema,
            Context context,

            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "schema"
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = LabelCreatedPayload.class, name = "com.example.webhook.model.payload.LabelCreatedPayload"),
                    @JsonSubTypes.Type(value = OrderCreatedPayload.class, name = "com.example.webhook.model.payload.OrderCreatedPayload"),
                    @JsonSubTypes.Type(value = PurchaseCreatedPayload.class, name = "com.example.webhook.model.payload.PurchaseCreatedPayload")
            })
            T payload
    ) {}
}
