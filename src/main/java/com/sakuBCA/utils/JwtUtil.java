package com.sakuBCA.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.config.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
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
    public String generateToken(UserDetailsImpl userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET_KEY.getBytes()));
    }


    // Verify Token
    // Verify Token
    public DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(SECRET_KEY.getBytes())) // Sesuai dengan generateToken
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
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            List<String> authorities = decodedJWT.getClaim("authorities").asList(String.class);

            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Gagal mengekstrak authorities dari token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    // Validate Token dengan Exception Handling yang lebih spesifik
    public boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);

            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new CustomException("Token telah kedaluwarsa", HttpStatus.UNAUTHORIZED);
            }

            return true;
        } catch (JWTVerificationException e) {
            throw new CustomException("Token tidak valid: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


}
