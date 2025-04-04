package com.sakuBCA.controllers;

import com.sakuBCA.dtos.authDTO.ChangePasswordRequest;
import com.sakuBCA.dtos.authDTO.LoginRequest;
import com.sakuBCA.dtos.customerDTO.RegisterCustomerRequestDTO;
import com.sakuBCA.dtos.authDTO.ResetPasswordRequest;
import com.sakuBCA.dtos.customerDTO.CustomerResponseDTO;
import com.sakuBCA.models.User;
import com.sakuBCA.services.AuthService;
import com.sakuBCA.services.CustomerDetailsService;
import com.sakuBCA.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerDetailsService customerDetailsService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@Valid @RequestBody LoginRequest loginRequestDto) {
        Map<String, Object> response = authService.authenticate(loginRequestDto);
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
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email tidak boleh kosong.");
        }

        userService.sendResetPasswordToken(email);
        return ResponseEntity.ok("Token reset password telah dikirim ke email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password berhasil diubah"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password berhasil diperbarui"));
    }
}

