package com.example.webhook.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    GroupedOpenApi webhookApi() {
        return GroupedOpenApi.builder()
                .group("webhook-apis")
                .pathsToMatch("/api/**")
                .build();
    }
}
