package com.example.webhook.config;

import com.example.webhook.core.properties.RetryProperties;
import com.example.webhook.core.properties.WebhookSchemaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        WebhookSchemaProperties.class,
        RetryProperties.class,
        WebhookSecurityProperties.class
})
public class PropertiesConfig {}
