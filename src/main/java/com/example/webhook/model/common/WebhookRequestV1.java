package com.example.webhook.model.common;

import com.example.webhook.model.payload.LabelCreatedPayload;
import com.example.webhook.model.payload.OrderCreatedPayload;
import com.example.webhook.model.payload.PurchaseCreatedPayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WebhookRequestV1<T> extends WebhookRequest {

    @JsonProperty("data")
    Data<T> data;

    @lombok.Data
    public static class Data<T> {
        @JsonProperty("type")
        String type;

        @JsonProperty("attributes")
        Attributes<T> attributes;

    }

    @lombok.Data
    public static class Attributes<T> {

        @JsonProperty("id")
        String id;

        @JsonProperty("name")
        String name;

        @JsonProperty("occuredOn")
        String occuredOn;

        @JsonProperty("contentType")
        String contentType;

        @JsonProperty("schema")
        String schema;

        @JsonProperty("payload")
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
        T payload;

    }
}
