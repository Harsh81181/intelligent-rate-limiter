package com.harsh.rate_limiter.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterKeyBuilder {

    @Value("${spring.profiles.active:local}")
    private static String environment;

    public static String buildKey(String userKey) {
        return String.format(
                "%s:rate-limiter:user:%s",
                environment,
                userKey
        );
    }
}

