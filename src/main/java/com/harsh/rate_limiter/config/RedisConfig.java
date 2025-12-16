package com.harsh.rate_limiter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
public class RedisConfig {
	private static String host;
	private static int port;
	@Value("${spring.data.redis.host}")
	private void setHost(String host) {
        this.host = host;
    }

	@Value("${spring.data.redis.port}")
	private void setPortt(int port) {
        this.port = port;
    }
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(host, port);

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(LettuceConnectionFactory connectionFactory) {

        return new ReactiveStringRedisTemplate(connectionFactory);
    }
}
