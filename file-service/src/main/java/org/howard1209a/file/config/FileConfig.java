package org.howard1209a.file.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.howard1209a.file.util.SnowflakeIdUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class FileConfig {

    @Bean
    public SnowflakeIdUtils snowflakeIdUtils() {
        return new SnowflakeIdUtils(3, 1);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
