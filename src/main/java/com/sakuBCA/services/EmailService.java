package com.sakuBCA.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    // 🔹 Kirim password sementara saat register pegawai
    public void sendInitialPasswordEmail(String to, String generatedPassword) {
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
        String subject = "Reset Password - SakuBCA";
        String body = "Anda telah meminta reset password.\n\n"
                + "Gunakan token berikut untuk mengatur ulang password Anda:\n"
                + resetToken + "\n\n"
                + "Token ini berlaku selama 15 menit.\n\n"
                + "Jika Anda tidak meminta reset password, abaikan email ini.";

        sendEmail(to, subject, body);
    }
}
