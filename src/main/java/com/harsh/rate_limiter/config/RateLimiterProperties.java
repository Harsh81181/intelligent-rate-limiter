package com.harsh.rate_limiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
@Getter
@Setter
public class RateLimiterProperties {
	/**
     * Sliding window size in milliseconds
     */
    private long windowMs;

    /**
     * Maximum requests allowed in the window
     */
    private int maxRequests;
}
