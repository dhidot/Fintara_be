package com.fintara.controllers;

import com.fintara.dtos.authDTO.*;
import com.fintara.dtos.customerDTO.RegisterCustomerRequestDTO;
import com.fintara.dtos.customerDTO.CustomerResponseDTO;
import com.fintara.services.AuthService;
import com.fintara.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired private AuthService authService;

    @PostMapping("/login-google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithGoogle(@RequestBody GoogleLoginRequestDTO request) {
        Map<String, Object> response = authService.loginWithGoogle(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.success("Login Google berhasil", response));
    }

    @PostMapping("/login-customer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginCustomer(@RequestBody LoginRequestCustomer request) {
        Map<String, Object> response = authService.loginCustomer(request);
        logger.debug("Sukses");
        logger.info("info");
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
    }

    @PostMapping("/login-pegawai")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginPegawai(@RequestBody LoginRequestPegawai request) {
        Map<String, Object> response = authService.loginPegawai(request);
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> registerCustomer(@Valid @RequestBody RegisterCustomerRequestDTO request) {
        CustomerResponseDTO result = authService.registerCustomer(request);
        return ResponseEntity.ok(ApiResponse.success("Registrasi berhasil", result));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logout berhasil"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST,"Email tidak boleh kosong."));
        }

        authService.sendResetPasswordToken(email);
        return ResponseEntity.ok(ApiResponse.success("Token reset password telah dikirim ke email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil diperbarui"));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyEmail(@RequestParam("token") String token) {
        Map<String, Object> response = authService.verifyEmail(token);
        HttpStatus status = (HttpStatus) response.getOrDefault("httpStatus", HttpStatus.OK);
        return ResponseEntity.status(status).body(ApiResponse.success("Email berhasil diverifikasi", response));
    }
}
