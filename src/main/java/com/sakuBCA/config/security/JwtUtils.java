package com.sakuBCA.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time-hour}")
    private int jwtExpirationHour;


    public String generateToken(Authentication authentication) {
        String username;
        if (authentication.getPrincipal() instanceof UserDetailsImpl userPrincipal) {
            username = userPrincipal.getUsername();
        } else {
            throw new IllegalArgumentException("Unsupported principal type");
        }
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, jwtExpirationHour);
        Date expiredDate = calendar.getTime();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(getSignKey())
                .compact();
    }

    public String getUsername(String jwt) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ") || bearerToken.length() <= 7) {
            throw new IllegalArgumentException("Invalid Bearer token format");
        }
        return bearerToken.substring(7).trim(); // Remove "Bearer " and trim any extraneous spaces
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

}