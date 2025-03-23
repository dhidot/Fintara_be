package com.sakuBCA.services;

import com.sakuBCA.dtos.PegawaiDetailsDTO;
import com.sakuBCA.dtos.UpdatePegawaiRequest;
import com.sakuBCA.dtos.UserWithPegawaiResponse;
import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.PegawaiDetailsRepository;
import com.sakuBCA.repositories.PegawaiRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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
                                String nip, Long branchId, StatusPegawai statusPegawai, String token) {
        // 🔹 Ambil user yang sedang login dari token
        String userEmail = jwtUtil.extractUsername(token);
        User loggedInUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Anda tidak memiliki izin untuk mendaftarkan pegawai"));

        // 🔹 Pastikan user yang login adalah Super Admin
        if (!"Super Admin".equals(loggedInUser.getRole().getName())) {
            throw new RuntimeException("Anda tidak memiliki izin untuk mendaftarkan pegawai");
        }

        // 🔹 Cek apakah role pegawai valid di database
        Role pegawaiRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Role pegawai tidak ditemukan"));

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
                .branchId(Math.toIntExact(branchId))
                .statusPegawai(StatusPegawai.valueOf(statusPegawai.name())) // Simpan status pegawai sebagai String
                .user(pegawai)
                .build();
        pegawaiRepository.save(pegawaiDetails);

        // 🔹 Kirim password ke email pegawai
        emailService.sendInitialPasswordEmail(email, generatedPassword);

        return pegawai;
    }

    public List<UserWithPegawaiResponse> getAllPegawai() {
        List<User> users = userRepository.findAllWithPegawai(); // Ambil User + PegawaiDetails
        return users.stream().map(user -> {
            UserWithPegawaiResponse response = new UserWithPegawaiResponse();
            response.setId(Long.valueOf(user.getId()));
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().getName()); // Ambil nama role
            response.setPegawaiDetails(user.getPegawaiDetails() != null ?
                    new PegawaiDetailsDTO(user.getPegawaiDetails()) : null);
            return response;
        }).collect(Collectors.toList());
    }

    public UserWithPegawaiResponse getPegawaiById(Long userId) {
        User user = userRepository.findByIdWithPegawai(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));

        UserWithPegawaiResponse response = new UserWithPegawaiResponse();
        response.setId(Long.valueOf(user.getId()));
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        // Konversi PegawaiDetails ke DTO jika ada
        response.setPegawaiDetails(user.getPegawaiDetails() != null ?
                new PegawaiDetailsDTO(user.getPegawaiDetails()) : null);

        return response;
    }

    //Edit Data Pegawai
    public UserWithPegawaiResponse updatePegawai(Long userId, UpdatePegawaiRequest request) {
        User user = userRepository.findByIdWithPegawai(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));

        // Update data User
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Update data PegawaiDetails jika ada
        if (user.getPegawaiDetails() != null) {
            PegawaiDetails pegawai = user.getPegawaiDetails();
            pegawai.setNip(request.getNip());
            pegawai.setBranchId(Integer.valueOf(request.getBranchId()));
            pegawai.setStatusPegawai(StatusPegawai.valueOf(request.getStatusPegawai()));
            pegawaiRepository.save(pegawai);
        }

        userRepository.save(user); // Simpan perubahan

        // Konversi ke response DTO
        UserWithPegawaiResponse response = new UserWithPegawaiResponse();
        response.setId(Long.valueOf(user.getId()));
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());
        response.setPegawaiDetails(new PegawaiDetailsDTO(user.getPegawaiDetails()));

        return response;
    }

    public void deletePegawai(Long id) {
        User user = userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));

        userRepository.delete(user);
    }
}
