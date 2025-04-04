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
    private static final String FIRST_LOGIN_PREFIX = "first_login:";

    public void saveSession(String email) {
        redisTemplate.opsForValue().set(email, "LOGGED_IN", SESSION_TIMEOUT, TimeUnit.SECONDS);
    }

    public boolean isUserLoggedIn(String email) {
        return redisTemplate.hasKey(email);
    }

    public void removeSession(String email) {
        redisTemplate.delete(email);
    }

    public void setFirstLoginStatus(String userId, boolean status) {
        redisTemplate.opsForValue().set(FIRST_LOGIN_PREFIX + userId, String.valueOf(status)); // 🔹 Simpan sebagai "true" atau "false"
    }

    public boolean getFirstLoginStatus(String userId) {
        String status = (String) redisTemplate.opsForValue().get(FIRST_LOGIN_PREFIX + userId);
        return status != null && Boolean.parseBoolean(status); // 🔹 Konversi kembali ke boolean
    }

    public void removeFirstLoginStatus(String userId) {
        redisTemplate.delete(FIRST_LOGIN_PREFIX + userId);
    }
}
