package com.sakuBCA.services;

import com.sakuBCA.config.security.JwtResponse;
import com.sakuBCA.config.security.UserDetailsImpl;
import com.sakuBCA.dtos.authDTO.LoginRequest;
import com.sakuBCA.dtos.customerDTO.RegisterCustomerRequestDTO;
import com.sakuBCA.dtos.superAdminDTO.CustomerResponseDTO;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public Map<String, Object> authenticate(LoginRequest loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = String.valueOf(userDetails.getUser().getRole().getName());
        List<String> features = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Buat JWT response
        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getUsername(), role, features);

        // Format response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");

        Map<String, Object> data = new HashMap<>();
        data.put("jwt", jwtResponse);
        response.put("data", data);

        return response;
    }


    @Transactional
    public CustomerResponseDTO registerCustomer(RegisterCustomerRequestDTO request) {
        // ⬇️ Cek apakah email sudah digunakan
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
        token = token.replace("Bearer ", ""); // Hapus prefix "Bearer "

        // Cek apakah token sudah di-blacklist
        if (tokenService.isTokenBlacklisted(token)) {
            throw new CustomException("Token sudah tidak valid", HttpStatus.BAD_REQUEST);
        }

        // Ambil expiry date dari token JWT
        Date expiryDate = jwtUtils.getExpirationDateFromToken(token);

        // Tambahkan token ke blacklist dengan expiry date yang sesuai
        tokenService.blacklistToken(token, LocalDateTime.now().plusSeconds(expiryDate.getTime() / 1000));

        System.out.println("Berhasil Logout");
    }

}