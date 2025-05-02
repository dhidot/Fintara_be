package com.sakuBCA.controllers;

import com.sakuBCA.dtos.authDTO.*;
import com.sakuBCA.dtos.customerDTO.RegisterCustomerRequestDTO;
import com.sakuBCA.dtos.customerDTO.CustomerResponseDTO;
import com.sakuBCA.services.AuthService;
import com.sakuBCA.services.CustomerDetailsService;
import com.sakuBCA.services.RedisService;
import com.sakuBCA.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private RedisService redisService;


    @PostMapping("/login-customer")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequestCustomer request) {
        Map<String, Object> response = authService.loginCustomer(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-pegawai")
    public ResponseEntity<?> loginPegawai(@RequestBody LoginRequestPegawai request) {
        Map<String, Object> response = authService.loginPegawai(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<CustomerResponseDTO> registerCustomer(@Valid @RequestBody RegisterCustomerRequestDTO request) {
        return ResponseEntity.ok(authService.registerCustomer(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logout berhasil.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("message", "Email tidak boleh kosong.")
            );
        }

        authService.sendResetPasswordToken(email);

        return ResponseEntity.ok(
                Collections.singletonMap("message", "Token reset password telah dikirim ke email.")
        );
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password berhasil diubah"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password berhasil diperbarui"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        Map<String, Object> response = authService.verifyEmail(token);
        return ResponseEntity.status((HttpStatus) response.getOrDefault("httpStatus", HttpStatus.OK)).body(response);
    }
}

