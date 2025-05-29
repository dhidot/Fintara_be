package com.fintara.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    // 🔹 Kirim email dengan HTML format
    public void sendEmail(String to, String subject, String htmlContent) {
        if (to == null || to.isEmpty() || subject == null || htmlContent == null) {
            logger.error("Gagal mengirim email: parameter tidak boleh null atau kosong.");
            throw new IllegalArgumentException("Email, subject, dan body tidak boleh kosong.");
        }

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            emailSender.send(message);
            logger.info("Email berhasil dikirim ke {}", to);
        } catch (MessagingException | MailException e) {
            logger.error("Gagal mengirim email ke {}: {}", to, e.getMessage());
            throw new RuntimeException("Gagal mengirim email, coba lagi nanti.");
        }
    }

    // 🔹 Kirim password sementara saat register pegawai
    @Async
    public void sendInitialPasswordEmail(String to, String generatedPassword) {
        if (generatedPassword == null || generatedPassword.isEmpty()) {
            logger.error("Gagal mengirim email: password sementara tidak boleh kosong untuk email {}", to);
            return; // Stop di sini, jangan kirim email
        }

        try {
            String subject = "Akun Pegawai Baru - Fintara";
            String body = "<html><body>"
                    + "<h2>Selamat, akun Anda telah dibuat.</h2>"
                    + "<p>Berikut adalah detail akun Anda:</p>"
                    + "<ul>"
                    + "<li><strong>Email:</strong> " + to + "</li>"
                    + "<li><strong>Password sementara:</strong> " + generatedPassword + "</li>"
                    + "</ul>"
                    + "<p>Harap segera masuk dan ubah password Anda.</p>"
                    + "<p>Terima kasih.</p>"
                    + "</body></html>";

            sendEmail(to, subject, body);
            logger.info("Berhasil mengirim email registrasi ke {}", to);
        } catch (Exception e) {
            logger.error("Gagal mengirim email ke {}: {}", to, e.getMessage(), e);
        }
    }


    // 🔹 Kirim token reset password ke email user
    public void sendResetPasswordEmail(String email, String resetLink) {
        String subject = "Reset Password Anda";
        String message = "<html><body>"
                + "<p>Klik link berikut untuk mengatur ulang password Anda:</p>"
                + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                + "<p>Jika Anda tidak meminta reset password, abaikan email ini.</p>"
                + "</body></html>";

        sendEmail(email, subject, message);
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        String subject = "Verifikasi Email Anda - Fintara";
        String body = "<html><body>"
                + "<p>Hai,</p>"
                + "<p>Terima kasih telah mendaftar di <strong>Fintara</strong>.</p>"
                + "<p>Silakan klik link di bawah ini untuk memverifikasi email Anda:</p>"
                + "<p><a href=\"" + verificationLink + "\">Verifikasi Email</a></p>"
                + "<p><em>Link ini berlaku selama 30 menit.</em></p>"
                + "</body></html>";

        sendEmail(to, subject, body);
    }

    // 🔹 Kirim email notifikasi dana dicairkan
    @Async
    public void sendLoanDisbursementEmail(String to, String customerName, String loanAmount) {
        String subject = "Pinjaman Anda Telah Dicairkan - Fintara";
        String body = "<html><body>"
                + "<h2>Hai, " + customerName + "</h2>"
                + "<p>Selamat! Pinjaman Anda telah berhasil dicairkan dengan nominal <strong>" + loanAmount + "</strong>.</p>"
                + "<p>Silakan cek detail transaksi di aplikasi Fintara Anda.</p>"
                + "<br>"
                + "<p>Terima kasih telah menggunakan layanan kami.</p>"
                + "<p><strong>Fintara</strong></p>"
                + "</body></html>";

        sendEmail(to, subject, body);
    }

}
