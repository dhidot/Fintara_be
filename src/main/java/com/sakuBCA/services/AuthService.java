package com.sakuBCA.services;

import com.sakuBCA.enums.UserType;
import com.sakuBCA.models.BlacklistedToken;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.BlacklistedTokenRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().getName());
    }

    @Transactional
    public User registerPegawai(String name, String email, String password, String pegawaiRoleName, String token) {
        // 🔹 Ambil user yang sedang login dari token
        String userEmail = jwtUtil.extractUsername(token);
        User loggedInUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Anda tidak memiliki izin untuk mendaftarkan pegawai"));

        // 🔹 Cek apakah user login adalah Super Admin
        if (!"Super Admin".equals(loggedInUser.getRole().getName())) {
            throw new RuntimeException("Anda tidak memiliki izin untuk mendaftarkan pegawai");
        }

        // 🔹 Pastikan role pegawai valid (Back Office, Branch Admin, Marketing)
        Role pegawaiRole = roleRepository.findByName(pegawaiRoleName)
                .orElseThrow(() -> new RuntimeException("Role pegawai tidak ditemukan"));

        // 🔹 Buat akun pegawai baru
        User pegawai = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .userType(UserType.PEGAWAI)
                .role(pegawaiRole)
                .build();

        return userRepository.save(pegawai);
    }


    @Transactional
    public User registerCustomer(String name, String email, String password) {
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER tidak ditemukan"));

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
            throw new RuntimeException("Token sudah tidak valid.");
        }

        // Tambahkan token ke blacklist
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedTokenRepository.save(blacklistedToken);

        System.out.println("Berhasil Logout");
    }
}