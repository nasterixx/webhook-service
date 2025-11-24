package com.example.webhook.core.dispatch;

import com.example.webhook.core.chain.GenericReactiveHandlerChain;
import com.example.webhook.core.chain.ReactiveWebhookHandler;
import com.example.webhook.core.properties.WebhookSchemaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactiveChainBuilder {

    private final ApplicationContext context;
    private final WebhookSchemaProperties schemaProperties;

    public ReactiveChainBuilder(ApplicationContext context, WebhookSchemaProperties schemaProperties) {
        this.context = context;
        this.schemaProperties = schemaProperties;
    }

    public WebhookSchemaProperties.SchemaMapping getSchemaMapping(String schema) {
        var mapping = schemaProperties.getSchemas().get(schema);
        if (mapping == null) {
            throw new IllegalArgumentException("No chain configuration for schema: " + schema);
        }
        return mapping;
    }

    public List<String> getHandlersForSchema(String schema) {
        return getSchemaMapping(schema).getHandlers();
    }

    @SuppressWarnings("unchecked")
    public GenericReactiveHandlerChain buildChain(String schema) {
        var names = getHandlersForSchema(schema);
        List<ReactiveWebhookHandler<Object, Object>> handlers = names.stream()
                .map(n -> (ReactiveWebhookHandler<Object, Object>) context.getBean(n))
                .collect(Collectors.toList());
        return new GenericReactiveHandlerChain(handlers);
    }
}
