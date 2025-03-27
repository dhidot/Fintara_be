package com.sakuBCA.controllers;

import com.sakuBCA.dtos.authDTO.LoginRequest;
import com.sakuBCA.dtos.customerDTO.RegisterCustomerRequest;
import com.sakuBCA.dtos.authDTO.ResetPasswordRequest;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.FeatureRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.services.AuthService;
import com.sakuBCA.services.UserService;
import com.sakuBCA.utils.JwtResponse;
import com.sakuBCA.utils.JwtUtils;
import com.sakuBCA.utils.UserDetailsImpl;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final FeatureRepository featureRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils, UserService userService,
                          AuthService authService, RoleRepository roleRepository,
                          FeatureRepository featureRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.featureRepository = featureRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@Valid @RequestBody LoginRequest loginRequestDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = String.valueOf(userDetails.getUser().getRole().getName());
        List<String> features = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");

        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getUsername(), role, features);

        Map<String, Object> data = new HashMap<>();
        data.put("jwt", jwtResponse);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<User> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        User user = authService.registerCustomer(request.getName(), request.getEmail(), request.getPassword());
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
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password berhasil direset.");
    }

}

