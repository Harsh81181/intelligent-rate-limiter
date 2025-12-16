package com.harsh.rate_limiter.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import com.harsh.rate_limiter.config.RateLimiterProperties;
import com.harsh.rate_limiter.dto.RateLimiterKeyBuilder;
import com.harsh.rate_limiter.dto.RateLimiterResultDto;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterLuaService {
	private final MeterRegistry meterRegistry;
	private  Counter allowedCounter;
	private  Counter blockedCounter;
	private  Counter fallbackCounter;

	private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<List> rateLimiterScript;
	private final RateLimiterProperties properties;
	
	@PostConstruct
	void initMetrics() {
	    this.allowedCounter = Counter.builder("rate_limiter.allowed")
	            .description("Allowed rate limiter requests")
	            .register(meterRegistry);

	    this.blockedCounter = Counter.builder("rate_limiter.blocked")
	            .description("Blocked rate limiter requests")
	            .register(meterRegistry);

	    this.fallbackCounter = Counter.builder("rate_limiter.fallback")
	            .description("Fallback when Redis is down")
	            .register(meterRegistry);
	}

	
	/** Sliding window (atomic via lua-script)
	    * @author Harsh
	    * @param key
	    * @return Mono
	    */
	public Mono<RateLimiterResultDto> isAllowed(String redisKey){
		long now=System.currentTimeMillis();
    	log.info("now performing lua script with redis Key = {}",redisKey);
    	
    	return redisTemplate.execute(rateLimiterScript,
    			List.of(redisKey),
    			List.of(String.valueOf(now),
    			String.valueOf(properties.getWindowMs()),
    			String.valueOf(properties.getMaxRequests())
    			)
    			).next().map(result->{
    				if (result==null ||result.size()<2) {
    				    log.error("Invalid rate limiter response from Redis");
    				    return new RateLimiterResultDto(false, 0);
    				}
    				int allowed=((Long) result.get(0)).intValue();
    				int remaining=((Long)result.get(1)).intValue();
    				if(allowed==1) {
    					allowedCounter.increment();
    					log.info("Rate limit allowed | key={} | remaining={}", redisKey, remaining);
    	                 return new RateLimiterResultDto(true, remaining);
    				}else {
    					blockedCounter.increment();
    					log.warn("Rate limit exceeded | key={}", redisKey);
    	                 return new RateLimiterResultDto(false, 0);
    				}
    			}).timeout(Duration.ofMillis(100)).onErrorResume(ex -> { // fail-open situation for redis allow all
    				fallbackCounter.increment();
    				log.error("Fallback metric incremented");
    		        log.error("Redis unavailable, FAIL-OPEN mode | key={}", redisKey, ex);
    		        return Mono.just(new RateLimiterResultDto(true, -1));
    		    });

	}
}
