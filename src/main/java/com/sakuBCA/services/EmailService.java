package com.sakuBCA.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        if (to == null || to.isEmpty() || subject == null || text == null) {
            logger.error("Gagal mengirim email: parameter tidak boleh null atau kosong.");
            throw new IllegalArgumentException("Email, subject, dan body tidak boleh kosong.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            logger.info("Email berhasil dikirim ke {}", to);
        } catch (MailException e) {
            logger.error("Gagal mengirim email ke {}: {}", to, e.getMessage());
            throw new RuntimeException("Gagal mengirim email, coba lagi nanti.");
        }
    }

    // 🔹 Kirim password sementara saat register pegawai
    public void sendInitialPasswordEmail(String to, String generatedPassword) {
        if (generatedPassword == null || generatedPassword.isEmpty()) {
            logger.error("Gagal mengirim email: password sementara tidak boleh kosong.");
            throw new IllegalArgumentException("Password sementara tidak boleh kosong.");
        }

        String subject = "Akun Pegawai Baru - SakuBCA";
        String body = "Selamat, akun Anda telah dibuat.\n\n"
                + "Berikut adalah detail akun Anda:\n"
                + "Email: " + to + "\n"
                + "Password sementara: " + generatedPassword + "\n\n"
                + "Harap segera masuk dan ubah password Anda.\n\n"
                + "Terima kasih.";

        sendEmail(to, subject, body);
    }

    // 🔹 Kirim token reset password ke email user
    public void sendResetPasswordToken(String to, String resetToken) {
        if (resetToken == null || resetToken.isEmpty()) {
            logger.error("Gagal mengirim email: token reset tidak boleh kosong.");
            throw new IllegalArgumentException("Token reset tidak boleh kosong.");
        }

        String subject = "Reset Password - SakuBCA";
        String body = "Anda telah meminta reset password.\n\n"
                + "Gunakan token berikut untuk mengatur ulang password Anda:\n"
                + resetToken + "\n\n"
                + "Token ini berlaku selama 15 menit.\n\n"
                + "Jika Anda tidak meminta reset password, abaikan email ini.";

        sendEmail(to, subject, body);
    }
}
