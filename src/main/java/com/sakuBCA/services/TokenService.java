package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.BlacklistedToken;
import com.sakuBCA.models.PasswordResetToken;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.BlacklistedTokenRepository;
import com.sakuBCA.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public TokenService(PasswordResetTokenRepository tokenRepository, BlacklistedTokenRepository blacklistedTokenRepository) {
        this.tokenRepository = tokenRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        tokenRepository.save(passwordResetToken);
        return token;
    }

    public PasswordResetToken validateToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("Token tidak valid atau sudah kedaluwarsa", HttpStatus.BAD_REQUEST));
    }

    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }

    public boolean tokenExist(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    public void blacklistToken(String token, LocalDateTime expiryDate) {
        if (blacklistedTokenRepository.existsByToken(token)) {
            throw new CustomException("Token sudah diblacklist", HttpStatus.BAD_REQUEST);
        }
        blacklistedTokenRepository.save(new BlacklistedToken(token, expiryDate));
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.findById(token)
                .map(blacklisted -> blacklisted.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Scheduled(cron = "0 0 * * * ?") // Jalankan setiap jam
    public void cleanupExpiredTokens() {
        blacklistedTokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }

}
