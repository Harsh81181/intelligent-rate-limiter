package com.harsh.rate_limiter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harsh.rate_limiter.service.RedisService;
import com.harsh.rate_limiter.service.RedisTestService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisTestController {

    @Autowired
    private RedisTestService redisTestService;
    @Autowired
    private final RedisService redisService;

    @GetMapping("/redis/test")
    public String testRedis() {
        return redisTestService.testConnection();
    }
    
    @PostMapping("/set/{key}/{value}")
    public Mono<String> setValue(@PathVariable String key, @PathVariable String value) {
        return redisService.setValue(key, value)
                .map(res -> "Saved: " + res);
    }

    @GetMapping("/get/{key}")
    public Mono<String> getValue(@PathVariable String key) {
        return redisService.getValue(key);
    }

    @DeleteMapping("/delete/{key}")
    public Mono<String> delete(@PathVariable String key) {
        return redisService.deleteKey(key)
                .map(res -> "Deleted: " + res);
    }
}
