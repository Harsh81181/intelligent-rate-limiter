package com.harsh.rate_limiter.service;

import java.time.Duration;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import com.harsh.rate_limiter.dto.RateLimiterKeyBuilder;
import com.harsh.rate_limiter.dto.RateLimiterResultDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final long WINDOW_SIZE_MS = 60_000;
    private static final long MAX_REQUESTS = 5;
   /** Sliding window (non-atomic)
    * @author Harsh
    * @param key
    * @return Mono
    */
    public Mono<RateLimiterResultDto> isAllowed(String key) {

    	String redisKey = RateLimiterKeyBuilder.buildKey(key);
        long now = System.currentTimeMillis();
        double windowStart = now - WINDOW_SIZE_MS;

        // 1. Remove old requests (outside sliding window)
        return redisTemplate.opsForZSet()
                .removeRangeByScore(
                        redisKey,
                        Range.closed(0.0, windowStart)
                )
                // 2. Count how many requests remain in window
                .then(redisTemplate.opsForZSet().size(redisKey))
                // 3. Decide allow or block
                .flatMap(currentRequestCount -> {
                    // BLOCK case
                    if (currentRequestCount >= MAX_REQUESTS) {
                    	log.warn(
                                "Rate limit exceeded | key={} | count={} | limit={}",
                                key, currentRequestCount, MAX_REQUESTS
                            );
                        return Mono.just(
                                new RateLimiterResultDto(false, 0)
                        );
                    }
                    // ALLOW case
                    log.info(
                            "Rate limit allowed | key={} | count={} | limit={}",
                            key, currentRequestCount, MAX_REQUESTS
                        );
                    return allowRequest(redisKey, now, currentRequestCount);
                });
    }
    
    private Mono<RateLimiterResultDto> allowRequest(
            String redisKey,
            long now,
            long currentRequestCount) {

        return redisTemplate.opsForZSet()
                .add(redisKey, String.valueOf(now), (double) now)
                // Set TTL so Redis cleans up automatically
                .then(redisTemplate.expire(
                        redisKey,
                        Duration.ofMillis(WINDOW_SIZE_MS)))
                // Return response
                .thenReturn(
                        new RateLimiterResultDto(
                                true,
                                MAX_REQUESTS - currentRequestCount - 1
                        )
                );
    }

}

