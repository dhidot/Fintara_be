package com.sakuBCA.services;

import com.sakuBCA.config.security.JwtResponse;
import com.sakuBCA.config.security.UserDetailsImpl;
import com.sakuBCA.dtos.authDTO.ChangePasswordRequest;
import com.sakuBCA.dtos.authDTO.LoginRequest;
import com.sakuBCA.dtos.authDTO.LoginRequestCustomer;
import com.sakuBCA.dtos.authDTO.LoginRequestPegawai;
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
import org.springframework.context.annotation.Bean;
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
import java.time.ZoneId;
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


    @Transactional
    public Map<String, Object> loginCustomer(LoginRequestCustomer request) {
        String rawEmail = request.getEmail();
        String email = rawEmail.toLowerCase().trim();  // Normalisasi email

        // Jika sudah login di perangkat lain, hapus session lama
        if (redisService.isCustomerLoggedIn(email)) {
            redisService.removeCustomerSession(email);
        }

        // Autentikasi user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        // Set auth context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT baru
        String jwt = jwtUtils.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Validasi tipe user
        if (user.getUserType() != UserType.CUSTOMER) {
            throw new CustomException("Akun ini bukan customer!", HttpStatus.UNAUTHORIZED);
        }

        // ✅ Gunakan helper handleFirstLogin()
        boolean isFirstLogin = handleFirstLogin(user);

        // Simpan session baru ke Redis
        redisService.saveCustomerSession(email, jwt);

        // Siapkan response
        JwtResponse jwtResponse = new JwtResponse(jwt, user.getEmail(), user.getRole().getName(), userDetails.getFeatures());
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");

        Map<String, Object> data = new HashMap<>();
        data.put("jwt", jwtResponse);
        data.put("firstLogin", isFirstLogin); // ← ✅ Kirim ke frontend

        response.put("data", data);

        return response;
    }

    @Transactional
    public Map<String, Object> loginPegawai(LoginRequestPegawai request) {
        String nip = request.getNip();

        if (redisService.isPegawaiLoggedIn(nip)) {
            throw new CustomException("Pegawai sudah login di perangkat lain!", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nip, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        if (user.getUserType() != UserType.PEGAWAI) {
            throw new CustomException("Akun ini bukan pegawai!", HttpStatus.UNAUTHORIZED);
        }

        // ✅ Gunakan helper handleFirstLogin
        boolean isFirstLogin = handleFirstLogin(user);

        redisService.savePegawaiSession(nip, jwt);

        JwtResponse jwtResponse = new JwtResponse(jwt, user.getEmail(), user.getRole().getName(), userDetails.getFeatures());

        Map<String, Object> data = new HashMap<>();
        data.put("jwt", jwtResponse);
        data.put("firstLogin", isFirstLogin);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
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
                .remainingPlafond(bronzePlafond.getMaxAmount())
                .build();

        // ⬇️ Simpan CustomerDetails
        customerDetailsService.saveCustomerDetails(customerDetails);

        // 🔹 Langsung kembalikan CustomerResponseDTO
        return CustomerResponseDTO.fromUser(customer, customerDetails);
    }

    @Transactional
    public void logout(String token) {
        String extractedToken = jwtUtils.extractToken(token);

        // Cek apakah token sudah diblacklist
        if (tokenService.isTokenBlacklisted(extractedToken)) {
            throw new CustomException("Token sudah tidak valid", HttpStatus.BAD_REQUEST);
        }

        // Ambil username (email atau NIP) dari JWT
        String username = jwtUtils.getUsername(extractedToken);
        User user = userService.getUserByEmailOrNip(username);

        // Cek expiry token dan blacklist jika belum expired
        Date expiryDate = jwtUtils.getExpirationDateFromToken(extractedToken);
        if (expiryDate.after(new Date())) {
            tokenService.blacklistToken(
                    extractedToken,
                    LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault())
            );
        }

        // Hapus session Redis sesuai userType
        if (user.getUserType() == UserType.PEGAWAI) {
            redisService.removePegawaiSession(user.getPegawaiDetails().getNip());
        } else if (user.getUserType() == UserType.CUSTOMER) {
            String email = user.getEmail().toLowerCase().trim();  // Normalisasi
            redisService.removeCustomerSession(email);
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userService.findByNip(request.getNip());

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomException("Password lama tidak cocok", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false);  // ✅ Setelah ganti password, set isFirstLogin = false
        userRepository.save(user);

        // ✅ Hapus status first login di Redis
        redisService.removeFirstLoginStatus(user.getId().toString());
    }

    public boolean handleFirstLogin(User user) {
        String userId = user.getId().toString();

        // Cek Redis dulu
        Boolean redisStatus = redisService.getFirstLoginStatus(userId);
        boolean isFirstLogin;

        if (redisStatus != null) {
            isFirstLogin = redisStatus;
        } else {
            // Ambil dari DB
            isFirstLogin = user.isFirstLogin();
            redisService.setFirstLoginStatus(userId, isFirstLogin);
        }

        // Jika firstLogin = true → update ke false
        if (isFirstLogin) {
            user.setFirstLogin(false);
            userRepository.save(user);
            redisService.setFirstLoginStatus(userId, false);
        }

        return isFirstLogin;
    }
}