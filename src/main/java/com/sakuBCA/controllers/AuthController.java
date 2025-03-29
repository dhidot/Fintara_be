package com.sakuBCA.controllers;

import com.sakuBCA.dtos.authDTO.LoginRequest;
import com.sakuBCA.dtos.customerDTO.RegisterCustomerRequest;
import com.sakuBCA.dtos.authDTO.ResetPasswordRequest;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.FeatureRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.services.AuthService;
import com.sakuBCA.services.UserService;
import com.sakuBCA.config.security.JwtResponse;
import com.sakuBCA.config.security.JwtUtils;
import com.sakuBCA.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@Valid @RequestBody LoginRequest loginRequestDto) {
        Map<String, Object> response = authService.authenticate(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<User> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        User user = authService.registerCustomer(request);
        return ResponseEntity.ok(user);
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
}

