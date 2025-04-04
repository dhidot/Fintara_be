package com.sakuBCA.services;

import com.sakuBCA.dtos.authDTO.ResetPasswordRequest;
import com.sakuBCA.dtos.customerDTO.CustomerDetailsDTO;
import com.sakuBCA.dtos.pegawaiDTO.PegawaiDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.UserResponseDTO;
import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.PasswordResetToken;
import com.sakuBCA.models.User;
import com.sakuBCA.models.Role;
import com.sakuBCA.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Get marketing by branch
    public List<User> getMarketingByBranch(UUID branchId) {
        return userRepository.findMarketingByBranch(branchId);
    }

    // Find user byID
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User tidak ditemukan!", HttpStatus.NOT_FOUND));
    }

    // Find user by Email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User dengan email " + email + " tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    // save
    public User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error saat menyimpan pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal menyimpan pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete user by id
    public void deleteUserById(UUID userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException("User dengan ID " + userId + " tidak ditemukan", HttpStatus.NOT_FOUND));
            userRepository.delete(user);
        } catch (Exception e) {
            logger.error("Error saat menghapus pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal menghapus pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public List<UserResponseDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            return users.stream().map(user -> new UserResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName(),
                    user.getCustomerDetails() != null ?
                            new CustomerDetailsDTO(user.getCustomerDetails()) : null,
                    user.getPegawaiDetails() != null ?
                            new PegawaiDetailsDTO(user.getPegawaiDetails()) : null
            )).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error saat mengambil daftar pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal mengambil daftar pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /********** SERVICE PEGWAWAI **********/
    //    Get user with usertype pegawai by id (use UserWithPegawaiResponse)
    public User getPegawaiUserById(UUID userId) {
        try {
            return userRepository.findByIdWithPegawai(userId)
                    .orElseThrow(() -> new CustomException("User dengan ID " + userId + " tidak ditemukan", HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error saat mengambil pengguna dengan ID {}: {}", userId, e.getMessage(), e);
            throw new CustomException("Gagal mengambil pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all pegawai (use UserWithPegawaiResponse)
    public List<User> getAllPegawai() {
        try {
            List<User> users = userRepository.findAllWithPegawai();
            if (users.isEmpty()) {
                logger.info("Tidak ada pengguna dengan role PEGAWAI ditemukan.");
            } else {
                logger.info("Pengguna dengan role PEGAWAI ditemukan: {}", users.size());
            }
            return users;
        } catch (Exception e) {
            logger.error("Error saat mengambil daftar pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal mengambil daftar pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public User getPegawaiByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("User dengan email " + email + " tidak ditemukan", HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error saat mengambil pengguna dengan email {}: {}", email, e.getMessage(), e);
            throw new CustomException("Gagal mengambil pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Role getRole(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return user.getRole(); // Pastikan User memiliki getRole()
    }

    // Get Role
    public boolean hasRole(UUID userId, String roleName) {
        Role role = getRole(userId);
        return role != null && role.getName().equalsIgnoreCase(roleName);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Get the username of the authenticated user
        return userRepository.findByEmail(username)  // Assuming email is used as the username
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.UNAUTHORIZED));
    }

    public UUID getBranchIdByUserId(UUID userId) {
        // Cari user berdasarkan ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));

        // Cek apakah user memiliki relasi dengan pegawai details (di mana branch ID tersimpan)
        if (user.getPegawaiDetails() == null || user.getPegawaiDetails().getBranch() == null) {
            throw new CustomException("User tidak terkait dengan cabang manapun", HttpStatus.BAD_REQUEST);
        }

        // Kembalikan branch ID
        return user.getPegawaiDetails().getBranch().getId();
    }


    /********** SERVICE CUSTOMER **********/
    // get all customer to be used at customerService
    public List<User> getAllCustomers() {
        try {
            List<User> users = userRepository.findAllWithCustomer();
            if (users.isEmpty()) {
                logger.info("Tidak ada pengguna dengan role CUSTOMER ditemukan.");
            } else {
                logger.info("Pengguna dengan role CUSTOMER ditemukan: {}", users.size());
            }
            return users;
        } catch (Exception e) {
            logger.error("Error saat mengambil daftar pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal mengambil daftar pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new CustomException("User dengan email " + username + " tidak ditemukan", HttpStatus.NOT_FOUND));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().getName())
                    .build();
        } catch (Exception e) {
            logger.error("Error saat memuat pengguna: {}", e.getMessage(), e);
            throw new CustomException("Gagal memuat data pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void sendResetPasswordToken(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));

            // 🔹 Generate token menggunakan TokenService
            String token = tokenService.generateToken(user);

            // 🔹 Buat link reset password
            String baseUrl = "https://yourfrontend.com/reset-password";
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

    public User getUserWithPegawaiDetails(UUID userId) {
        return userRepository.findUserWithPegawaiDetailsById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }
}

