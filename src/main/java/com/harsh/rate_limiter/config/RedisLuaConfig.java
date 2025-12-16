package com.harsh.rate_limiter.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisLuaConfig {

	@Bean
    public RedisScript<List> rateLimiterScript() {
        RedisScript<List> script = new DefaultRedisScript();
        ((DefaultRedisScript<List>) script)
                .setScriptSource(
                        new ResourceScriptSource(
                                new ClassPathResource("lua/rate_limiter.lua")
                        )
                );
        ((DefaultRedisScript<List>) script).setResultType(List.class);
        return script;
    }
}
