package com.sakuBCA.services;

import com.sakuBCA.dtos.superAdminDTO.PegawaiDetailsDTO;
import com.sakuBCA.dtos.superAdminDTO.UpdatePegawaiRequest;
import com.sakuBCA.dtos.superAdminDTO.UserWithPegawaiResponse;
import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.Branch;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.PegawaiRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PegawaiService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PegawaiRepository pegawaiRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @PreAuthorize(("hasAuthority('Super Admin')"))
    @Transactional
    public User registerPegawai(String name, String email, String role,
                                String nip, Branch branch, StatusPegawai statusPegawai, String token) {
        // 🔹 Ambil user yang sedang login dari token
        String userEmail = jwtUtil.extractUsername(token);
        User loggedInUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("Anda tidak memiliki akses untuk mendaftarkan pegawai", HttpStatus.FORBIDDEN));

        // 🔹 Pastikan user yang login adalah Super Admin
        if (!"Super Admin".equals(loggedInUser.getRole().getName())) {
            throw new CustomException("Anda tidak memiliki izin untuk mendaftarkan pegawai", HttpStatus.FORBIDDEN);
        }

        // 🔹 Cek apakah role pegawai valid di database
        Role pegawaiRole = roleRepository.findByName(role)
                .orElseThrow(() -> new CustomException("Role pegawai tidak ditemukan", HttpStatus.NOT_FOUND));

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
        userRepository.save(pegawai);

        // 🔹 Buat PegawaiDetails baru
        PegawaiDetails pegawaiDetails = PegawaiDetails.builder()
                .nip(nip)
                .branch(branch)
                .statusPegawai(StatusPegawai.valueOf(statusPegawai.name())) // Simpan status pegawai sebagai String
                .user(pegawai)
                .build();
        pegawaiRepository.save(pegawaiDetails);

        // 🔹 Kirim password ke email pegawai
        emailService.sendInitialPasswordEmail(email, generatedPassword);

        return pegawai;
    }

    public List<UserWithPegawaiResponse> getAllPegawai() {
        try {
            List<User> users = userRepository.findAllWithPegawai(); // Ambil User + PegawaiDetails

            if (users.isEmpty()) {
                throw new CustomException("Tidak ada data pegawai yang ditemukan", HttpStatus.NOT_FOUND);
            }

            return users.stream().map(user -> {
                UserWithPegawaiResponse response = new UserWithPegawaiResponse();

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


    public UserWithPegawaiResponse getPegawaiById(UUID userId) {
        User user = userRepository.findByIdWithPegawai(userId)
                .orElseThrow(() -> new CustomException("User dengan ID " + userId + " tidak ditemukan", HttpStatus.NOT_FOUND));

        UserWithPegawaiResponse response = new UserWithPegawaiResponse();
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
    public UserWithPegawaiResponse updatePegawai(UUID userId, UpdatePegawaiRequest request) {
        User user = userRepository.findByIdWithPegawai(userId)
                .orElseThrow(() -> new CustomException("User dengan ID " + userId + " tidak ditemukan", HttpStatus.BAD_REQUEST));

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

        userRepository.save(user); // Simpan perubahan

        // Konversi ke response DTO
        UserWithPegawaiResponse response = new UserWithPegawaiResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());
        response.setPegawaiDetails(new PegawaiDetailsDTO(user.getPegawaiDetails()));

        return response;
    }

    public void deletePegawai(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User dengan ID " + id + " tidak ditemukan", HttpStatus.BAD_REQUEST));

        userRepository.delete(user);
    }
}
