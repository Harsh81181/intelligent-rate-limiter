package com.harsh.rate_limiter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateLimiterResultDto {
    private boolean allowed;
    private long remaining;
}
