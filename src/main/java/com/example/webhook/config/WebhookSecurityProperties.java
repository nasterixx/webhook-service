package com.example.webhook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.webhook")
public class WebhookSecurityProperties {

    private String expectedToken;
    private List<String> requiredHeaders;

    public String getExpectedToken() {
        return expectedToken;
    }

    public void setExpectedToken(String expectedToken) {
        this.expectedToken = expectedToken;
    }

    public List<String> getRequiredHeaders() {
        return requiredHeaders;
    }

    public void setRequiredHeaders(List<String> requiredHeaders) {
        this.requiredHeaders = requiredHeaders;
    }
}
