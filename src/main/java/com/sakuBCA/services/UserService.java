package com.sakuBCA.services;

import com.sakuBCA.dtos.CustomerDetailsDTO;
import com.sakuBCA.dtos.PegawaiDetailsDTO;
import com.sakuBCA.dtos.UserResponseDTO;
import com.sakuBCA.models.PasswordResetToken;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.PasswordResetTokenRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final JavaMailSender emailSender;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, PasswordResetTokenRepository tokenRepository,
                       JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.emailSender = emailSender;
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
                                user.getCustomerDetails().getId(),
                                user.getCustomerDetails().getTtl(),
                                user.getCustomerDetails().getAlamat(),
                                user.getCustomerDetails().getNoTelp(),
                                user.getCustomerDetails().getNik(),
                                user.getCustomerDetails().getNamaIbuKandung(),
                                user.getCustomerDetails().getPekerjaan(),
                                user.getCustomerDetails().getGaji(),
                                user.getCustomerDetails().getNoRek(),
                                user.getCustomerDetails().getStatusRumah(),
                                user.getCustomerDetails().getPlafond()
                        )
                        : null,
                user.getPegawaiDetails() != null ?
                        new PegawaiDetailsDTO(
                                user.getPegawaiDetails().getId(),
                                user.getPegawaiDetails().getNip(),
                                user.getPegawaiDetails().getBranchId(),
                                user.getPegawaiDetails().getStatusPegawai()
                        )
                        : null
        )).collect(Collectors.toList());
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void sendResetPasswordToken(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Generate token unik
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        tokenRepository.save(passwordResetToken);

        // Kirim ke email user
        String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token;
        sendEmail(user.getEmail(), resetUrl);
    }

    private void sendEmail(String emailTo, String resetUrl){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailTo);
        message.setSubject("Reset Password");
        message.setText("Klik link berikut untuk mereset password: " + resetUrl);
        emailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token tidak valid atau sudah kadaluarsa"));

        User user = resetToken.getUser();
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken); // Hapus token setelah digunakan
    }

}
