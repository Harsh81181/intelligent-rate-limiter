package com.harsh.rate_limiter.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.harsh.rate_limiter.dto.RateLimiterResultDto;

@SpringBootTest
@ActiveProfiles("test")
class RateLimiterServiceTest {

    @Autowired
    private RateLimiterService rateLimiterService;
    @Test
    void shouldBlockAfterMaxRequests() {

        String key = "test-user";

        for (int i = 0; i < 5; i++) {
            RateLimiterResultDto result =
                    rateLimiterService.isAllowed(key).block();
            assertTrue(result.isAllowed());
        }

        RateLimiterResultDto blocked =
                rateLimiterService.isAllowed(key).block();

        assertFalse(blocked.isAllowed());
    }

}
