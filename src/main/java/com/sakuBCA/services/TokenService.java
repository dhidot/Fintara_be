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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(token.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
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

    public void blacklistToken(String token, LocalDateTime expiryDate) {
        String hashedToken = hashToken(token);
        if (blacklistedTokenRepository.existsByToken(hashedToken)) {
            throw new CustomException("Token sudah diblacklist", HttpStatus.BAD_REQUEST);
        }
        blacklistedTokenRepository.save(new BlacklistedToken(hashedToken, expiryDate));
    }

    public boolean isTokenBlacklisted(String token) {
        String hashedToken = hashToken(token);
        return blacklistedTokenRepository.findById(hashedToken)
                .map(blacklisted -> blacklisted.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }


    @Scheduled(cron = "0 0 * * * ?") // Jalankan setiap jam
    public void cleanupExpiredTokens() {
        blacklistedTokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }

}
