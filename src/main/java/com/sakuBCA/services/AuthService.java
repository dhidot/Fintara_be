package com.sakuBCA.services;

import com.sakuBCA.enums.UserType;
import com.sakuBCA.dtos.exceptions.CustomException;
import com.sakuBCA.models.BlacklistedToken;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.BlacklistedTokenRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("Email atau password salah", HttpStatus.NOT_FOUND);
        }

        // Ambil authorities dari user
        List<String> authorities = List.of(user.getRole().getName());

        return jwtUtil.generateToken(user.getEmail(), authorities);
    }

    @Transactional
    public User registerCustomer(String name, String email, String password) {
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new CustomException("Role CUSTOMER tidak ditemukan", HttpStatus.NOT_FOUND));

        User customer = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .userType(UserType.CUSTOMER) // ⬅️ Pastikan userType CUSTOMER
                .role(customerRole)
                .build();

        return userRepository.save(customer);
    }

    @Transactional
    public void logout(String token) {
        token = token.replace("Bearer ", ""); // Hapus prefix "Bearer "

        // Cek apakah token sudah di-blacklist
        if (blacklistedTokenRepository.existsByToken(token)) {
            throw new CustomException("Token sudah tidak valid", HttpStatus.BAD_REQUEST);
        }

        // Tambahkan token ke blacklist
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedTokenRepository.save(blacklistedToken);

        System.out.println("Berhasil Logout");
    }
}