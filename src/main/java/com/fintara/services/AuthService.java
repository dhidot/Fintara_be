package com.fintara.services;

import com.fintara.security.JwtResponse;
import com.fintara.security.UserDetailsImpl;
import com.fintara.dtos.authDTO.*;
import com.fintara.dtos.customerDTO.RegisterCustomerRequestDTO;
import com.fintara.dtos.customerDTO.CustomerResponseDTO;
import com.fintara.enums.UserType;
import com.fintara.exceptions.CustomException;
import com.fintara.models.*;
import com.fintara.repositories.UserRepository;
import com.fintara.utils.GoogleTokenVerifier;
import com.fintara.utils.JwtUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private EmailService emailService;
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
    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // CUSTOMER AUTHENTICATION
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
                .isFirstLogin(true)
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

        // 🔹 Generate token verifikasi dan simpan ke Redis
        String verificationToken = UUID.randomUUID().toString();
        redisService.saveEmailVerificationToken(verificationToken, customer.getEmail());

        String verificationLink = "localhost:4200/#/verify-email?token=" + verificationToken;
        emailService.sendVerificationEmail(customer.getEmail(), verificationLink);

        // 🔹 Langsung kembalikan CustomerResponseDTO
        return CustomerResponseDTO.fromUser(customer, customerDetails);
    }

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

        // Ambil user dari principal
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 🔒 Cek apakah email sudah diverifikasi
        if (!user.isEmailVerified()) {
            throw new CustomException("Email belum diverifikasi! Silakan cek email Anda.", HttpStatus.UNAUTHORIZED);
        }

        // 🔒 Validasi tipe user
        if (user.getUserType() != UserType.CUSTOMER) {
            throw new CustomException("Akun ini bukan customer!", HttpStatus.UNAUTHORIZED);
        }

        // ✅ Gunakan helper handleFirstLogin()
        boolean isFirstLogin = handleFirstLogin(user);

        // 🔐 Simpan session baru ke Redis
        String jwt = jwtUtils.generateToken(authentication);
        redisService.saveCustomerSession(email, jwt);

        // Siapkan response
        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                user.getEmail(),
                user.getRole().getName(),
                userDetails.getFeatures(),
                user.getName()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwtResponse);
        response.put("firstLogin", isFirstLogin);

        return response;
    }

    @Transactional
    public Map<String, Object> loginWithGoogle(String idToken) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(idToken); // ⬅️ helper di bawah

        if (payload == null) {
            throw new CustomException("ID Token Google tidak valid", HttpStatus.UNAUTHORIZED);
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // 🔄 Daftarkan user baru otomatis
            Role customerRole = roleService.getRoleByName("CUSTOMER");

            user = User.builder()
                    .name(name)
                    .email(email)
                    .password("") // Kosong karena tidak login manual
                    .userType(UserType.CUSTOMER)
                    .emailVerified(true)
                    .isFirstLogin(true)
                    .fotoUrl(pictureUrl)
                    .role(customerRole)
                    .build();

            user = userRepository.save(user);

            Plafond plafond = plafondService.getPlafondByName("Bronze");
            CustomerDetails details = CustomerDetails.builder()
                    .user(user)
                    .plafond(plafond)
                    .remainingPlafond(plafond.getMaxAmount())
                    .build();

            customerDetailsService.saveCustomerDetails(details);
        }

        // 🔐 Simpan session dan buat JWT
        String jwt = jwtUtils.generateTokenForGoogle(user);

        redisService.saveCustomerSession(email, jwt);

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", new JwtResponse(jwt, email, user.getRole().getName(), getFeatures(user.getRole().getRoleFeatures()), user.getName()));
        response.put("firstLogin", user.isFirstLogin());

        return response;
    }

    public List<String> getFeatures(List<RoleFeature> roleFeature) {
        // get list feature by role
        return roleFeature.stream()
                .map(roleFeature1 -> roleFeature1.getFeature().getName())
                .collect(Collectors.toList());
    }


    // EMPLOYEE AUTHENTICATION
    @Transactional
    public Map<String, Object> loginPegawai(LoginRequestPegawai request) {
        String nip = request.getNip();

        // Check if the employee is already logged in from another device
        if (redisService.isPegawaiLoggedIn(nip)) {
            throw new CustomException("Pegawai sudah login di perangkat lain!", HttpStatus.BAD_REQUEST);
        }

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nip, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtils.generateToken(authentication);

        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Check if the user is a pegawai (employee)
        if (user.getUserType() != UserType.PEGAWAI) {
            throw new CustomException("Akun ini bukan pegawai!", HttpStatus.UNAUTHORIZED);
        }

        // Handle first login logic
        boolean isFirstLogin = handleFirstLogin(user);

        // Save session to Redis
        redisService.savePegawaiSession(nip, jwt);

        // Prepare JWT response
        JwtResponse jwtResponse = new JwtResponse(jwt, user.getEmail(), user.getRole().getName(), userDetails.getFeatures(), user.getName());

        // Prepare the response without redundant "data" key
        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwtResponse);
        response.put("firstLogin", isFirstLogin);

        return response;
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
        User user = userService.getAuthenticatedUser(); // ambil user yang sedang login

        // Validasi password lama
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomException("Password lama tidak cocok", HttpStatus.BAD_REQUEST);
        }

        // Validasi password baru dan konfirmasi
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new CustomException("Konfirmasi password baru tidak cocok", HttpStatus.BAD_REQUEST);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false); // jika ada first login logic
        userRepository.save(user);

        // Hapus status first login jika kamu pakai Redis
        redisService.removeFirstLoginStatus(user.getId().toString());
    }

    public void sendResetPasswordToken(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));

            // 🔹 Generate token menggunakan TokenService
            String token = tokenService.generateToken(user);

            // 🔹 Buat link reset password
            String baseUrl = "localhost:4200/#/reset-password"; // Ganti dengan URL frontend kamu
            String resetLink = baseUrl + "?token=" + token;

            // 🔹 Kirim email dengan link reset password
            emailService.sendResetPasswordEmail(email, resetLink);

        } catch (CustomException e) {
            logger.error("Kesalahan bisnis: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error saat mengirim token reset password: {}", e.getMessage(), e);
            throw new CustomException("Gagal mengirim token reset password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        try {
            // 1️⃣ Validasi password baru dan konfirmasi
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new CustomException("Password baru dan konfirmasi tidak cocok", HttpStatus.BAD_REQUEST);
            }

            // 2️⃣ Validasi token menggunakan TokenService
            PasswordResetToken resetToken = tokenService.validateToken(request.getToken());

            // 3️⃣ Update password user
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // 4️⃣ Hapus token setelah digunakan
            tokenService.deleteToken(resetToken);

            logger.info("Password berhasil diubah untuk user: {}", user.getEmail());

        } catch (CustomException e) {
            logger.error("Kesalahan bisnis: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error saat mereset password: {}", e.getMessage(), e);
            throw new CustomException("Gagal mereset password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

        return isFirstLogin;
    }

    @Transactional
    public Map<String, Object> verifyEmail(String token) {
        String email = redisService.getEmailByVerificationToken(token);
        if (email == null) {
            return Map.of(
                    "status", "error",
                    "message", "Token tidak valid atau sudah kedaluwarsa",
                    "httpStatus", HttpStatus.BAD_REQUEST
            );
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return Map.of(
                    "status", "error",
                    "message", "User tidak ditemukan",
                    "httpStatus", HttpStatus.NOT_FOUND
            );
        }

        if (user.isEmailVerified()) {
            return Map.of(
                    "status", "success",
                    "message", "Email sudah diverifikasi sebelumnya.",
                    "httpStatus", HttpStatus.OK
            );
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        redisService.removeEmailVerificationToken(token);

        return Map.of(
                "status", "success",
                "message", "Email berhasil diverifikasi.",
                "httpStatus", HttpStatus.OK
        );
    }
}