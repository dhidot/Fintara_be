package com.fintara.services;

import com.fintara.dtos.customerDTO.CustomerDetailsDTO;
import com.fintara.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.fintara.dtos.pegawaiDTO.PegawaiDetailsDTO;
import com.fintara.dtos.superAdminDTO.UserResponseDTO;
import com.fintara.exceptions.CustomException;
import com.fintara.models.User;
import com.fintara.models.Role;
import com.fintara.repositories.UserRepository;
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

    // Find user by NIP
    public User findByNip(String nip) {
        return userRepository.findByPegawaiDetails_Nip(nip)
                .orElseThrow(() -> new CustomException("User dengan NIP " + nip + " tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    // Find user by Email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User dengan email " + email + " tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    // Find user by email or nip
    public User getUserByEmailOrNip(String username) {
        return userRepository.findByEmailOrNip(username)
                .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    // Exists by email
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
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

    public User getPegawaiByNip(String nip) {
        try {
            return userRepository.findByPegawaiDetails_Nip(nip)
                    .orElseThrow(() -> new CustomException("User dengan NIP " + nip + " tidak ditemukan", HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error saat mengambil pengguna dengan NIP {}: {}", nip, e.getMessage(), e);
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
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.UNAUTHORIZED));
    }

    public UUID getBranchIdByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User tidak ditemukan", HttpStatus.NOT_FOUND));

        if (user.getPegawaiDetails() == null || user.getPegawaiDetails().getBranch() == null) {
            throw new CustomException("User tidak terkait dengan cabang manapun", HttpStatus.BAD_REQUEST);
        }

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

    public UserWithCustomerResponseDTO getCustomerUserById(UUID userId) {
        try {
            User user = userRepository.findByIdWithCustomer(userId)
                    .orElseThrow(() -> new CustomException("User dengan ID " + userId + " tidak ditemukan", HttpStatus.NOT_FOUND));

            return new UserWithCustomerResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName(),
                    user.getJenisKelamin(),
                    new CustomerDetailsDTO(user.getCustomerDetails())
            );
        } catch (Exception e) {
            logger.error("Error saat mengambil pengguna dengan ID {}: {}", userId, e.getMessage(), e);
            throw new CustomException("Gagal mengambil pengguna", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

