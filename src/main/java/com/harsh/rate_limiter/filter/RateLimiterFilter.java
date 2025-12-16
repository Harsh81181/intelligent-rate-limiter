package com.harsh.rate_limiter.filter;
import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.harsh.rate_limiter.config.RateLimiterProperties;
import com.harsh.rate_limiter.dto.RateLimiterKeyBuilder;
import com.harsh.rate_limiter.dto.RateLimiterResultDto;
import com.harsh.rate_limiter.service.RateLimiterLuaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterLuaService rateLimiterLuaService;
    @Value("${rate-limiter.excluded-paths}")
    private String excludedPaths;
    @Autowired
    private RateLimiterProperties properties;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
    	String requestPath = request.getRequestURI();

    	if (isExcludedPath(requestPath)) {
    	    log.info("Skipping rate limit for path: {}", requestPath);
    	    filterChain.doFilter(request, response);
    	    return;
    	}

    	String rateLimitKey = RateLimiterKeyBuilder.buildKey(request);

        RateLimiterResultDto result =
                rateLimiterLuaService.isAllowed(rateLimitKey).block();

        if (result == null || !result.isAllowed()) {
        	addRateLimitHeaders(response, 0);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        addRateLimitHeaders(response,result.getRemaining());
        filterChain.doFilter(request, response);
    }
    
    private void addRateLimitHeaders(HttpServletResponse response,long remaining) {
    	response.setHeader("X-Rate-Limiter-Limit",String.valueOf(properties.getMaxRequests()));
    	response.setHeader("X-Rate-Limiter-Remaining", String.valueOf(Math.max(remaining, 0)));
    	response.setHeader("X-Rate-Limiter-Reset", String.valueOf(System.currentTimeMillis()+properties.getWindowMs()));
    }
    
    private boolean isExcludedPath(String requestPath) {
        return Arrays.stream(excludedPaths.split(","))
                .anyMatch(requestPath::startsWith);
    }

}
