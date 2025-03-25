package com.sakuBCA.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sakuBCA.config.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "iniAdalahSecretKeySangatRahasiaDanPanjangBanget12345";
    private static final long EXPIRATION_TIME = 86400000; // 1 Hari

    // Generate Token dengan authorities sebagai array
    public String generateToken(String email, List<String> authorities) {
        return JWT.create()
                .withSubject(email)
                .withClaim("authorities", authorities) // Ubah dari role ke authorities array
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
            throw new CustomException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }

    // Extract Username
    public String extractUsername(String token) {
        return verifyToken(token).getSubject();
    }

    // Extract Authorities dari JWT
    public List<GrantedAuthority> extractAuthorities(String token) {
        List<String> authorities = verifyToken(token).getClaim("authorities").asList(String.class);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Validate Token dengan Exception Handling yang lebih spesifik
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            String username = decodedJWT.getSubject();

            if (!username.equals(userDetails.getUsername())) {
                throw new CustomException("Token tidak sesuai dengan pengguna", HttpStatus.UNAUTHORIZED);
            }

            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new CustomException("Token telah kedaluwarsa", HttpStatus.UNAUTHORIZED);
            }

            return true;
        } catch (JWTVerificationException e) {
            throw new CustomException("Token tidak valid: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new CustomException("Kesalahan validasi token: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

}
