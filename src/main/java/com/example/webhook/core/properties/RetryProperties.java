package com.example.webhook.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Global retry configuration: attempts, delay, multiplier.
 * Per-schema 'retry.enabled' + 'retry.strategy' + 'retry.retryOn' decide how it's used.
 */
@ConfigurationProperties(prefix = "webhook.retry")
public class RetryProperties {

    private int maxAttempts = 3;
    private long initialDelayMs = 1000;
    private double multiplier = 2.0;

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getInitialDelayMs() {
        return initialDelayMs;
    }

    public void setInitialDelayMs(long initialDelayMs) {
        this.initialDelayMs = initialDelayMs;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
