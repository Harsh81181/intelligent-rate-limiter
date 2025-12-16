package com.harsh.rate_limiter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Boolean> setValue(String key, String value) {
        return redisTemplate.opsForValue().set(key, value);
    }

    public Mono<String> getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> deleteKey(String key) {
        return redisTemplate.opsForValue().delete(key);
    }
}
