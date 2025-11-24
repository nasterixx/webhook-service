package com.example.webhook.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "webhook")
public class WebhookSchemaProperties {

    private Map<String, SchemaMapping> schemas = new HashMap<>();

    public Map<String, SchemaMapping> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, SchemaMapping> schemas) {
        this.schemas = schemas;
    }

    public static class SchemaMapping {
        private String className;
        private List<String> handlers;
        private SchemaRetry retry; // per-schema retry (optional)

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public List<String> getHandlers() {
            return handlers;
        }

        public void setHandlers(List<String> handlers) {
            this.handlers = handlers;
        }

        public SchemaRetry getRetry() {
            return retry;
        }

        public void setRetry(SchemaRetry retry) {
            this.retry = retry;
        }
    }

    public static class SchemaRetry {
        private Boolean enabled;  // optional; null = disabled
        private String strategy;  // "fixed", "backoff", "jitter"

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getStrategy() {
            return strategy;
        }

        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }
    }
}
