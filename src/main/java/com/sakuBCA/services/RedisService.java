package com.sakuBCA.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final long SESSION_TIMEOUT = 3600; // 1 jam

    public void saveSession(String email) {
        redisTemplate.opsForValue().set(email, "LOGGED_IN", SESSION_TIMEOUT, TimeUnit.SECONDS);
    }

    public boolean isUserLoggedIn(String email) {
        return redisTemplate.hasKey(email);
    }

    public void removeSession(String email) {
        redisTemplate.delete(email);
    }
}
