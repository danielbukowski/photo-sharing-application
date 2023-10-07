package com.danielbukowski.photosharing.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Value("${REDIS_HOST}")
    private String host;
    @Value("${REDIS_PASSWORD}")
    private String password;
    @Value("${REDIS_PORT}")
    private int port;

    @Bean
    public LettuceConnectionFactory connectionFactory() {

        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(host);
        redisConf.setPort(port);
        redisConf.setPassword(password);

        return new LettuceConnectionFactory(redisConf);
    }

}