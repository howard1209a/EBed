package org.howard1209a.user.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void setObject(String key, Object value) {
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getObject(String key, Class<T> c) {
        String json = stringRedisTemplate.opsForValue().get(key);
        try {
            return objectMapper.readValue(json, c);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHashObject(String key, String hashKey, Object value) {
        try {
            stringRedisTemplate.opsForHash().put(key, hashKey, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getHashObject(String key, String hashKey, Class<T> c) {
        String json = (String) stringRedisTemplate.opsForHash().get(key, hashKey);
        try {
            return objectMapper.readValue(json, c);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHashObject(String key, String hashKey) {
        return (String) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    public long hashIncrement(String key, String hashKey, long num) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, num);
    }

    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public void setExpireTime(String key, long timeOut, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, timeOut, timeUnit);
    }
}
