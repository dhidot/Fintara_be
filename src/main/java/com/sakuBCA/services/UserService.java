package com.sakuBCA.services;

import com.sakuBCA.dtos.superAdminDTO.CustomerDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.PegawaiDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.UserResponseDTO;
import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.PasswordResetToken;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.PasswordResetTokenRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, PasswordResetTokenRepository tokenRepository,
                       EmailService emailservice) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailservice;
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            return users.stream().map(user -> new UserResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName(),
                    user.getCustomerDetails() != null ?
                            new CustomerDetailsDTO(user.getCustomerDetails()) : null,
                    user.getPegawaiDetails() != null ?
                            new PegawaiDetailsDTO(user.getPegawaiDetails()) : null
            )).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error saat mengambil daftar pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal mengambil daftar pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new CustomException("User dengan email " + username + " tidak ditemukan", HttpStatus.NOT_FOUND));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().getName())
                    .build();
        } catch (Exception e) {
            logger.error("Error saat memuat pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal memuat data pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void sendResetPasswordToken(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));

            // Generate token unik
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
            tokenRepository.save(passwordResetToken);

            // 🔹 Kirim email token reset password
            emailService.sendResetPasswordToken(email, token);

        } catch (CustomException e) {
            logger.error("Kesalahan bisnis: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error saat mengirim token reset password: {}", e.getMessage(), e);
            throw new CustomException("Gagal mengirim token reset password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void resetPassword(String token, String newPassword) {
        try {
            PasswordResetToken resetToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new CustomException("Token tidak valid atau sudah kedaluwarsa", HttpStatus.BAD_REQUEST));

            User user = resetToken.getUser();
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userRepository.save(user);

            tokenRepository.delete(resetToken); // Hapus token setelah digunakan

        } catch (CustomException e) {
            logger.error("Kesalahan bisnis: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error saat mereset password: {}", e.getMessage(), e);
            throw new CustomException("Gagal mereset password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
