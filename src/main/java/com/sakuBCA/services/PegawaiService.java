package com.sakuBCA.services;

import com.sakuBCA.dtos.superAdminDTO.PegawaiDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.UpdatePegawaiRequestDTO;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PegawaiService {
    private final PasswordEncoder passwordEncoder;
    private final PegawaiRepository pegawaiRepository;
    private final PegawaiDetailsRepository pegawaiDetailsRepository;
    private final BranchService branchService;
    private final RoleService roleService;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;
    

    @Transactional
    public User registerPegawai(String name, String email, String role,
                                String nip, UUID branchId, StatusPegawai statusPegawai) {
        // 🔹 Cek apakah ada branch dengan ID yang diberikan
        Branch existingBranch = branchService.findBranchById(branchId);

        // 🔹 Cek apakah role pegawai valid di database
        Role pegawaiRole = roleService.getRoleByName(role);

        // 🔹 Autogenerate password sementara (8 karakter alfanumerik)
        String generatedPassword = RandomStringUtils.randomAlphanumeric(8);

        // 🔹 Buat akun pegawai baru
        User pegawai = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(generatedPassword)) // Simpan password terenkripsi
                .role(pegawaiRole)
                .userType(UserType.PEGAWAI)
                .build();
        userService.saveUser(pegawai);

        // 🔹 Buat PegawaiDetails baru
        PegawaiDetails pegawaiDetails = PegawaiDetails.builder()
                .nip(nip)
                .branch(existingBranch)
                .statusPegawai(StatusPegawai.valueOf(statusPegawai.name())) // Simpan status pegawai sebagai String
                .user(pegawai)
                .build();
        pegawaiRepository.save(pegawaiDetails);

        // 🔹 Kirim password ke email pegawai
        emailService.sendInitialPasswordEmail(email, generatedPassword);

        return pegawai;
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
        response.setRole(user.getRole().getName());

        // Konversi PegawaiDetails ke DTO jika ada
        response.setPegawaiDetails(user.getPegawaiDetails() != null ?
                new PegawaiDetailsDTO(user.getPegawaiDetails()) : null);

        return response;
    }

    //Edit Data Pegawai
    public UserWithPegawaiResponseDTO updatePegawai(UUID userId, UpdatePegawaiRequestDTO request) {
        User user = userService.getPegawaiUserById(userId);

        // Update data User
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Update data PegawaiDetails jika ada
        if (user.getPegawaiDetails() != null) {
            PegawaiDetails pegawai = user.getPegawaiDetails();
            pegawai.setNip(request.getNip());
//            pegawai.setBranchId(Integer.valueOf(request.getBranchId()));
            pegawai.setStatusPegawai(StatusPegawai.valueOf(request.getStatusPegawai()));
            pegawaiRepository.save(pegawai);
        }

        userService.saveUser(user); // Simpan perubahan

        // Konversi ke response DTO
        UserWithPegawaiResponseDTO response = new UserWithPegawaiResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());
        response.setPegawaiDetails(new PegawaiDetailsDTO(user.getPegawaiDetails()));

        return response;
    }

    public void deletePegawai(UUID id) {
        User user = userService.getPegawaiUserById(id);
        userService.deleteUserById(user.getId());
    }
}
