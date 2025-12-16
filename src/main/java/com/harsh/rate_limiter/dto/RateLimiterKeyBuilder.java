package com.harsh.rate_limiter.dto;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
@Component
public class RateLimiterKeyBuilder {
    private static String environment;

    @Value("${spring.profiles.active}")
    private void setEnvironment(String env) {
        environment = env;
    }
    
    public static String buildKey(HttpServletRequest request) {
    	String apiKey=request.getHeader("X-API-KEY");
    	if(apiKey!=null && !apiKey.isBlank()) {
    		return formateKey("api", apiKey);
    	}
    	String userId=request.getHeader("X-USER-ID");
    	if(userId!=null && !userId.isBlank()) {
    		return formateKey("user", userId);
    	}
    	String ip=request.getRemoteAddr();
    	return formateKey("ip", ip);
    }

    private static String formateKey(String type,String value) {
    	return String.format(
                "%s:rate-limiter:%s:%s",
                environment,
                type,
                value
        );
    	}
}

