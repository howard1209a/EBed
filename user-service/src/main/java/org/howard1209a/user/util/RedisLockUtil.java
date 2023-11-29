package org.howard1209a.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLockUtil {
    public static final String LOCK_KEY = "lock:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean tryLock(String s) { // 非阻塞式获取锁
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_KEY + s, "");
        if (success) {
            return true;
        } else {
            return false;
        }
    }

    public void unlock(String s) { // 释放锁
        stringRedisTemplate.delete(LOCK_KEY + s);
    }

    public void blockingGetLock(String s) { // 阻塞式获取锁，低效
        while (!tryLock(s)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
