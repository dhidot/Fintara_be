package com.fintara.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final long SESSION_TIMEOUT = 3600; // 1 jam
    private static final Duration SESSION_TTL = Duration.ofDays(7);
    private static final String PEGAWAI_SESSION_PREFIX = "pegawai_session:";
    private static final String CUSTOMER_SESSION_PREFIX = "customer_session:";
    private static final String FIRST_LOGIN_PREFIX = "first_login:";
    private static final String VERIFY_EMAIL_PREFIX = "verify_email:";
    private static final long VERIFY_EMAIL_TTL_MINUTES = 30;

    // ---------- PEGAWAI ----------
    public void savePegawaiSession(String nip, String jwtToken) {
        redisTemplate.opsForValue().set(PEGAWAI_SESSION_PREFIX + nip, jwtToken, SESSION_TTL);
    }

    public boolean isPegawaiLoggedIn(String nip) {
        return redisTemplate.hasKey(PEGAWAI_SESSION_PREFIX + nip);
    }

    public void removePegawaiSession(String nip) {
        redisTemplate.delete(PEGAWAI_SESSION_PREFIX + nip);
    }

    // ---------- CUSTOMER ----------
    public void saveCustomerSession(String email, String jwtToken) {
        redisTemplate.opsForValue().set(CUSTOMER_SESSION_PREFIX + email, jwtToken, SESSION_TTL);
    }

    public boolean isCustomerLoggedIn(String email) {
        return redisTemplate.hasKey(CUSTOMER_SESSION_PREFIX + email);
    }

    public void removeCustomerSession(String email) {
        redisTemplate.delete(CUSTOMER_SESSION_PREFIX + email);
    }

    // ---------- FIRST LOGIN FLAG ----------
    public void setFirstLoginStatus(String userId, boolean status) {
        redisTemplate.opsForValue().set(FIRST_LOGIN_PREFIX + userId, String.valueOf(status));
    }

    public Boolean getFirstLoginStatus(String userId) {
        String status = redisTemplate.opsForValue().get(FIRST_LOGIN_PREFIX + userId);
        return status != null ? Boolean.valueOf(status) : null;
    }

    public void removeFirstLoginStatus(String userId) {
        redisTemplate.delete(FIRST_LOGIN_PREFIX + userId);
    }

    // ---------- VERIFY EMAIL ----------
    public void saveEmailVerificationToken(String token, String email) {
        redisTemplate.opsForValue().set(VERIFY_EMAIL_PREFIX + token, email, VERIFY_EMAIL_TTL_MINUTES, TimeUnit.MINUTES);
    }

    public String getEmailByVerificationToken(String token) {
        return redisTemplate.opsForValue().get(VERIFY_EMAIL_PREFIX + token);
    }

    public void removeEmailVerificationToken(String token) {
        redisTemplate.delete(VERIFY_EMAIL_PREFIX + token);
    }
}
