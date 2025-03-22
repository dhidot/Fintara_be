package com.sakuBCA.controllers;

import com.sakuBCA.dtos.LoginRequest;
import com.sakuBCA.dtos.RegisterCustomerRequest;
import com.sakuBCA.dtos.RegisterPegawaiRequest;
import com.sakuBCA.dtos.ResetPasswordRequest;
import com.sakuBCA.models.BlacklistedToken;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.BlacklistedTokenRepository;
import com.sakuBCA.services.AuthService;
import com.sakuBCA.services.UserService;
import com.sakuBCA.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register/customer")
    public ResponseEntity<User> registerCustomer(@RequestBody RegisterCustomerRequest request) {
        return ResponseEntity.ok(authService.registerCustomer(request.getName(), request.getEmail(), request.getPassword()));
    }

    @PreAuthorize("hasAuthority('Super Admin')")
    @PostMapping("/register/pegawai")
    public ResponseEntity<User> registerPegawai(
            @RequestHeader("Authorization") String token,
            @RequestBody RegisterPegawaiRequest request) {

        User pegawai = authService.registerPegawai(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                token.replace("Bearer ", "")
        );
        return ResponseEntity.ok(pegawai);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logout berhasil.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email tidak boleh kosong.");
        }

        userService.sendResetPasswordToken(email);
        return ResponseEntity.ok("Token reset password telah dikirim ke email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password berhasil direset.");
    }

}

