package com.example.webhook.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WebhookRequestV2<T> extends WebhookRequest {

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

        @JsonProperty("context")
        Context context;

        @JsonProperty("payload")
        T payload;

    }
}
