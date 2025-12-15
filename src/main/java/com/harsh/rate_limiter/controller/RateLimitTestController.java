package com.harsh.rate_limiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.harsh.rate_limiter.service.RateLimiterLuaService;
//import com.harsh.rate_limiter.service.RateLimiterService;

import lombok.RequiredArgsConstructor;
//import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RateLimitTestController {

    //private final RateLimiterService rateLimiterService;
    //private final RateLimiterLuaService rateLimiterService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {

    	return ResponseEntity.ok("Request successful");
    }
}

