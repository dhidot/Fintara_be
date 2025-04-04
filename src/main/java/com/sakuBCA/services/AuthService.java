package com.sakuBCA.services;

import com.sakuBCA.config.security.JwtResponse;
import com.sakuBCA.config.security.UserDetailsImpl;
import com.sakuBCA.dtos.authDTO.ChangePasswordRequest;
import com.sakuBCA.dtos.authDTO.LoginRequest;
import com.sakuBCA.dtos.customerDTO.RegisterCustomerRequestDTO;
import com.sakuBCA.dtos.customerDTO.CustomerResponseDTO;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.*;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.config.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PlafondService plafondService;
    @Autowired
    private CustomerDetailsService customerDetailsService;


    @Autowired
    private RedisService redisService;

    public Map<String, Object> authenticate(LoginRequest loginRequestDto) {
        String email = loginRequestDto.getEmail();

        if (redisService.isUserLoggedIn(email)) {
            throw new RuntimeException("User sudah login!");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        // ✅ Cek apakah user adalah Pegawai dan first login
        boolean isFirstLogin = user.isFirstLogin() && user.getUserType() == UserType.PEGAWAI;

        // ✅ Simpan status first login hanya jika user adalah pegawai
        redisService.setFirstLoginStatus(user.getId().toString(), isFirstLogin);

        JwtResponse jwtResponse = new JwtResponse(jwt, user.getEmail(), user.getRole().getName(), userDetails.getFeatures());
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");

        Map<String, Object> data = new HashMap<>();
        data.put("jwt", jwtResponse);
        data.put("isFirstLogin", isFirstLogin);  // ✅ Kirim ke frontend
        response.put("data", data);

        return response;
    }


    @Transactional
    public CustomerResponseDTO registerCustomer(RegisterCustomerRequestDTO request) {
        // Cek apakah email sudah pernah didaftarkan
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email sudah terdaftar!", HttpStatus.BAD_REQUEST);
        }

        Role customerRole = roleService.getRoleByName("CUSTOMER");

        User customer = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.CUSTOMER)
                .role(customerRole)
                .build();

        // ⬇️ Simpan User dulu
        userRepository.save(customer);

        // ⬇️ Ambil Plafond default (Bronze)
        Plafond bronzePlafond = plafondService.getPlafondByName("Bronze");

        // ⬇️ Buat CustomerDetails dengan Plafond default
        CustomerDetails customerDetails = CustomerDetails.builder()
                .user(customer)
                .plafond(bronzePlafond)
                .build();

        // ⬇️ Simpan CustomerDetails
        customerDetailsService.saveCustomerDetails(customerDetails);

        // 🔹 Langsung kembalikan CustomerResponseDTO
        return CustomerResponseDTO.fromUser(customer, customerDetails);
    }


    @Transactional
    public void logout(String token) {
        Logger logger = LoggerFactory.getLogger(AuthService.class);

        String extractedToken = jwtUtils.extractToken(token); // Hapus prefix "Bearer "

        // Cek apakah token sudah di-blacklist
        if (tokenService.isTokenBlacklisted(extractedToken)) {
            throw new CustomException("Token sudah tidak valid", HttpStatus.BAD_REQUEST);
        }

        // Ambil email dari token JWT
        String email = jwtUtils.getUsername(extractedToken);

        // Ambil expiry date dari token JWT
        Date expiryDate = jwtUtils.getExpirationDateFromToken(extractedToken);

        // Tambahkan token ke blacklist dengan expiry date yang sesuai
        tokenService.blacklistToken(extractedToken, LocalDateTime.now().plusSeconds(expiryDate.getTime() / 1000));

        // Hapus sesi login dari Redis agar user bisa login lagi
        redisService.removeSession(email);

        logger.info("User dengan email {} berhasil logout", email);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomException("Password lama tidak cocok", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false);  // ✅ Setelah ganti password, set isFirstLogin = false
        userRepository.save(user);

        // ✅ Hapus status first login di Redis
        redisService.removeFirstLoginStatus(user.getId().toString());
    }

}