package com.sakuBCA.services;

import com.sakuBCA.dtos.pegawaiDTO.PegawaiDetailsDTO;
import com.sakuBCA.dtos.pegawaiDTO.RegisterPegawaiRequestDTO;
import com.sakuBCA.dtos.pegawaiDTO.RegisterPegawaiResponseDTO;
import com.sakuBCA.dtos.pegawaiDTO.UpdatePegawaiRequestDTO;
import com.sakuBCA.dtos.superAdminDTO.UserWithPegawaiResponseDTO;
import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.Branch;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.*;
import com.sakuBCA.config.security.JwtUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PegawaiService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PegawaiRepository pegawaiRepository;
    @Autowired
    private BranchService branchService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtils jwtUtils;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public RegisterPegawaiResponseDTO registerPegawai(RegisterPegawaiRequestDTO request) {
        if (pegawaiRepository.existsByNip(request.getNip())) {
            throw new DuplicateKeyException("NIP sudah terdaftar");
        }

        // Validasi apakah email sudah terdaftar
        if (userService.existsByEmail(request.getEmail())) {
            throw new DuplicateKeyException("Email sudah terdaftar");
        }

        Branch existingBranch = branchService.findBranchByName(request.getBranchName());
        Role pegawaiRole = roleService.getRoleByName(request.getRole());
        String generatedPassword = RandomStringUtils.randomAlphanumeric(8);

        User pegawai = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .jenisKelamin(request.getJenisKelamin())
                .password(passwordEncoder.encode(generatedPassword))
                .role(pegawaiRole)
                .userType(UserType.PEGAWAI)
                .isFirstLogin(true)  // ✅ Set isFirstLogin saat registrasi
                .build();
        userService.saveUser(pegawai);

        PegawaiDetails pegawaiDetails = PegawaiDetails.builder()
                .nip(request.getNip())
                .branch(existingBranch)
                .statusPegawai(request.getStatusPegawai())
                .user(pegawai)
                .build();
        pegawaiRepository.save(pegawaiDetails);

        emailService.sendInitialPasswordEmail(request.getEmail(), generatedPassword);

        // 🔹 Return response minimal
        return new RegisterPegawaiResponseDTO(
                pegawai.getEmail(),
                "Akun pegawai berhasil dibuat. Silakan cek email untuk password. dan segera login untuk mengubah password."
        );
    }

    public List<UserWithPegawaiResponseDTO> getAllPegawai() {
        try {
            List<User> users = userService.getAllPegawai(); // Ambil User + PegawaiDetails

            if (users.isEmpty()) {
                throw new CustomException("Tidak ada data pegawai yang ditemukan", HttpStatus.NOT_FOUND);
            }

            return users.stream().map(user -> {
                UserWithPegawaiResponseDTO response = new UserWithPegawaiResponseDTO();

                response.setId(user.getId());
                response.setName(user.getName());
                response.setEmail(user.getEmail());
                response.setJenisKelamin(user.getJenisKelamin());

                // Pastikan role tidak null sebelum diakses
                if (user.getRole() != null) {
                    response.setRole(user.getRole().getName());
                } else {
                    response.setRole("ROLE_UNKNOWN"); // Default jika role null
                }

                // Set PegawaiDetails jika ada
                response.setPegawaiDetails(user.getPegawaiDetails() != null ?
                        new PegawaiDetailsDTO(user.getPegawaiDetails()) : null);

                return response;
            }).collect(Collectors.toList());

        } catch (CustomException e) {
            throw e; // CustomException tetap dilempar agar bisa ditangani oleh controller
        } catch (Exception e) {
            // Tangani kesalahan tidak terduga dan log error
            throw new CustomException("Terjadi kesalahan saat mengambil data pegawai", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserWithPegawaiResponseDTO getPegawaiById(UUID userId) {
        User user = userService.getPegawaiUserById(userId);

        UserWithPegawaiResponseDTO response = new UserWithPegawaiResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setJenisKelamin(user.getJenisKelamin());
        response.setRole(user.getRole().getName());

        // Konversi PegawaiDetails ke DTO jika ada
        response.setPegawaiDetails(user.getPegawaiDetails() != null ?
                new PegawaiDetailsDTO(user.getPegawaiDetails()) : null);

        return response;
    }

    public UserWithPegawaiResponseDTO getMyProfile() {
        // Mengambil username atau ID pengguna yang sedang login dari SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername(); // Username biasanya adalah email atau ID pengguna

        // Ambil User berdasarkan username
        User user = userService.findByEmail(username);

        return mapToUserWithPegawaiResponseDTO(user);
    }

    private UserWithPegawaiResponseDTO mapToUserWithPegawaiResponseDTO(User user) {
        UserWithPegawaiResponseDTO response = new UserWithPegawaiResponseDTO();
        response.setId(user.getId());
        response.setFotoUrl(user.getFotoUrl());
        response.setName(user.getName());
        response.setJenisKelamin(user.getJenisKelamin());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        if (user.getPegawaiDetails() != null) {
            response.setPegawaiDetails(new PegawaiDetailsDTO(user.getPegawaiDetails()));
        }

        return response;
    }

    //Edit Data Pegawai
    @Transactional
    public UserWithPegawaiResponseDTO updatePegawai(UUID userId, UpdatePegawaiRequestDTO request, String token) {
        // 🔍 Ambil email user yang sedang login dari token JWT
        String extractToken = jwtUtils.extractToken(token);
        String email = jwtUtils.getUsername(extractToken);

        // 🔍 Cari user yang sedang login
        User loggedInUser = userService.findByEmail(email);

        // 🔍 Cari user yang ingin diperbarui
        User targetUser = userService.getPegawaiUserById(userId);

        // ✅ Validasi: Hanya user itu sendiri atau super admin yang bisa mengupdate
        if (!loggedInUser.getId().equals(targetUser.getId()) &&
                !loggedInUser.getRole().getName().equalsIgnoreCase("SUPER_ADMIN")) {
            throw new CustomException("Anda tidak memiliki izin untuk mengedit data ini", HttpStatus.FORBIDDEN);
        }

        // 🔄 Update data User
        targetUser.setName(request.getName());
        targetUser.setEmail(request.getEmail());

        // 🔄 Update data PegawaiDetails jika ada
        if (targetUser.getPegawaiDetails() != null) {
            PegawaiDetails pegawai = targetUser.getPegawaiDetails();
            pegawai.setNip(request.getNip());

            // 🔍 Cari Branch berdasarkan nama
            Branch branch = branchService.findBranchByName(request.getBranch());
            pegawai.setBranch(branch);

            pegawai.setStatusPegawai(StatusPegawai.valueOf(request.getStatusPegawai()));
            pegawaiRepository.save(pegawai);
        }

        // Simpan perubahan user
        userService.saveUser(targetUser);

        // 🔄 Konversi ke response DTO tanpa constructor
        UserWithPegawaiResponseDTO response = new UserWithPegawaiResponseDTO();
        response.setId(targetUser.getId());
        response.setName(targetUser.getName());
        response.setEmail(targetUser.getEmail());
        response.setRole(targetUser.getRole().getName());

        if (targetUser.getPegawaiDetails() != null) {
            response.setPegawaiDetails(new PegawaiDetailsDTO(targetUser.getPegawaiDetails()));
        }

        return response;
    }



    public void deletePegawai(UUID id) {
        User user = userService.getPegawaiUserById(id);
        userService.deleteUserById(user.getId());
    }

    public Long count() {
        return pegawaiRepository.count();
    }
}
