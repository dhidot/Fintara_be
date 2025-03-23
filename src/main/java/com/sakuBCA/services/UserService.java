package com.sakuBCA.services;

import com.sakuBCA.dtos.superAdminDTO.CustomerDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.PegawaiDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.UserResponseDTO;
import com.sakuBCA.dtos.exceptions.CustomException;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, PasswordResetTokenRepository tokenRepository,
                       JavaMailSender emailSender, EmailService emailservice) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailservice;
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getCustomerDetails() != null ?
                        new CustomerDetailsDTO(
                                user.getCustomerDetails()
                        )
                        : null,
                user.getPegawaiDetails() != null ?
                        new PegawaiDetailsDTO(
                                user.getPegawaiDetails()
                        )
                        : null
        )).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException("User dengan email " + username + " tidak ditemukan", HttpStatus.NOT_FOUND));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName()) // Pastikan role sudah dikonversi ke bentuk yang benar
                .build();
    }

    public void sendResetPasswordToken(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));

        // Generate token unik
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        tokenRepository.save(passwordResetToken);

        // 🔹 Kirim email token reset password
        emailService.sendResetPasswordToken(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("Token tidak valid", HttpStatus.BAD_REQUEST));

        User user = resetToken.getUser();
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken); // Hapus token setelah digunakan
    }

}
