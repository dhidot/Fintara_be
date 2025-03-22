package com.sakuBCA.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "iniAdalahSecretKeySangatRahasiaDanPanjangBanget12345";
    private static final long EXPIRATION_TIME = 86400000; // 1 Hari

    // Generate Token
    public String generateToken(String email, String role) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    // Verify Token
    public DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }

    // Extract Role
    public String extractRole(String token) {
        return verifyToken(token).getClaim("role").asString();
    }

    public String extractUsername(String token) {
        return verifyToken(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            String username = decodedJWT.getSubject();
            return username.equals(userDetails.getUsername()) && !decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
