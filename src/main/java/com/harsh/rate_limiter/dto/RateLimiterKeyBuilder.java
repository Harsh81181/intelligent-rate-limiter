package com.harsh.rate_limiter.dto;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
@Component
public class RateLimiterKeyBuilder {
    private static String environment;

    @Value("${spring.profiles.active}")
    private void setEnvironment(String env) {
        environment = env;
    }

    public static String buildKey(String userKey) {
        return environment + ":rate-limiter:user:" + userKey;
    }
}

