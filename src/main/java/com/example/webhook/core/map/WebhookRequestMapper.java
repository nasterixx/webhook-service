package com.example.webhook.core.map;

import com.example.webhook.core.properties.WebhookSchemaProperties;
import com.example.webhook.model.common.WebhookRequestV1;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.stereotype.Component;

@Component
public class WebhookRequestMapper {

    private final ObjectMapper objectMapper;
    private final WebhookSchemaProperties schemaProperties;

    public WebhookRequestMapper(ObjectMapper objectMapper, WebhookSchemaProperties schemaProperties) {
        this.objectMapper = objectMapper;
        this.schemaProperties = schemaProperties;
    }

    public WebhookRequestV1<?> map(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        String schema = root.path("data").path("attributes").path("contentSchema").asText();

        var mapping = schemaProperties.getSchemas().get(schema);
        if (mapping == null) {
            throw new IllegalArgumentException("Unknown contentSchema: " + schema);
        }

        Class<?> payloadClass = Class.forName(mapping.getClassName());
        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(WebhookRequestV1.class, payloadClass);

        return objectMapper.readValue(json, type);
    }
}
