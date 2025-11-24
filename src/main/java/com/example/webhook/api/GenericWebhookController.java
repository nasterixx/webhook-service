package com.example.webhook.api;

import com.example.webhook.core.dispatch.WebhookDispatcher;
import com.example.webhook.core.map.WebhookRequestMapper;
import com.example.webhook.core.properties.WebhookSchemaProperties;
import com.example.webhook.model.common.WebhookRequestV1;
import com.example.webhook.model.common.WebhookRequestV2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/{version}")
public class GenericWebhookController {

    private final WebhookRequestMapper mapper;
    private final WebhookDispatcher dispatcher;
    private final ObjectMapper objectMapper;
    private final WebhookSchemaProperties schemaProperties;

    public GenericWebhookController(WebhookRequestMapper mapper, WebhookDispatcher dispatcher, ObjectMapper objectMapper, WebhookSchemaProperties schemaProperties) {
        this.mapper = mapper;
        this.dispatcher = dispatcher;
        this.objectMapper = objectMapper;
        this.schemaProperties = schemaProperties;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> receive(
            @PathVariable("version") String version,
            @RequestBody String json) throws JsonProcessingException, ClassNotFoundException {

        JsonNode root = objectMapper.readTree(json);
        String schema = root.path("data").path("attributes").path("schema").asText();

        var mapping = schemaProperties.getSchemas().get(schema);

        if (mapping == null) {
            throw new IllegalArgumentException("Unknown schema: " + schema);
        }

        switch (version) {
            case "v1": {

                Class<?> payloadClass = Class.forName(mapping.getClassName());
                JavaType type = objectMapper.getTypeFactory().constructParametricType(WebhookRequestV1.class, payloadClass);

                WebhookRequestV1<?> requestV1 = objectMapper.readValue(json, type);

                /*JavaType type = objectMapper.getTypeFactory()
                        .constructParametricType(WebhookRequestV1.class, Class.forName(WebhookRequestV1.class.getName()));
//                        .constructParametricType(WebhookRequestV1.class, Class.forName("com.example.webhook.api.WebhookRequestV1"));
                WebhookRequestV1<?> requestV1 = objectMapper.readValue(body, type);*/
                return Mono.fromCallable(() -> requestV1)
                        .flatMap(dispatcher::dispatchV1)
                        .map(status -> ResponseEntity.ok("Processed successfully (" + version + "): " + status));
            }
            case "v2": {

                Class<?> payloadClass = Class.forName(mapping.getClassName());
                JavaType type = objectMapper.getTypeFactory().constructParametricType(WebhookRequestV2.class, payloadClass);

                WebhookRequestV2<?> requestV2 = objectMapper.readValue(json, type);


                /*JavaType type = objectMapper.getTypeFactory()
                        .constructParametricType(WebhookRequestV2.class, Class.forName(WebhookRequestV2.class.getName()));
                WebhookRequestV2<?> requestV2 = objectMapper.readValue(body, type);*/
                return Mono.fromCallable(() -> requestV2)
                        .flatMap(dispatcher::dispatchV2)
                        .map(status -> ResponseEntity.ok("Processed successfully (" + version + "): " + status));
            }
            default: {
                throw new IllegalArgumentException("Unknown version: " + version);
            }
        }

       /* return Mono.fromCallable(() -> (WebhookRequestV1<?>) mapper.map(body))
                .flatMap(dispatcher::dispatchV1)
                .map(status -> ResponseEntity.ok("Processed successfully (" + version + "): " + status));*/
    }
}
