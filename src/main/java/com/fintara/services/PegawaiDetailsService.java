package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.utils.JwtUtils;
import com.fintara.dtos.pegawaiDTO.PegawaiDetailsRequestDTO;
import com.fintara.enums.StatusPegawai;
import com.fintara.models.Branch;
import com.fintara.models.PegawaiDetails;
import com.fintara.models.Role;
import com.fintara.models.User;
import com.fintara.repositories.PegawaiDetailsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PegawaiDetailsService {
    @Autowired
    private PegawaiDetailsRepository pegawaiDetailsRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private RoleService roleService;


    public PegawaiDetailsService(PegawaiDetailsRepository pegawaiDetailsRepository) {
        this.pegawaiDetailsRepository = pegawaiDetailsRepository;
    }

    // find pegawai details by user
    public PegawaiDetails getPegawaiDetailsByUser(User user) {
        return pegawaiDetailsRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Pegawai details not found", HttpStatus.NOT_FOUND));
    }

    // Save pegawai details
    @Transactional
    public PegawaiDetails savePegawaiDetails(PegawaiDetails pegawaiDetails) {
        return pegawaiDetailsRepository.save(pegawaiDetails);
    }

    @Transactional
    public String updatePegawaiDetails(String token, UUID idPegawai, PegawaiDetailsRequestDTO request) {
        // Ambil email dari token
        String email = jwtUtils.getUsername(jwtUtils.extractToken(token));
        User user = userService.getPegawaiByEmail(email);

        User targetUser;

        // Jika user adalah super admin, gunakan ID pegawai yang dikirim
        if (userService.hasRole(user.getId(), "SUPER_ADMIN")) {
            targetUser = userService.getPegawaiUserById(idPegawai);
        } else {
            // Jika bukan super admin, hanya bisa edit dirinya sendiri
            if (!user.getId().equals(idPegawai)) {
                throw new CustomException("Anda tidak memiliki akses untuk mengedit data ini", HttpStatus.FORBIDDEN);
            }
            targetUser = user;
        }

        // Cek apakah target user punya detail pegawai
        PegawaiDetails pegawaiDetails = getPegawaiDetailsByUser(targetUser);

        // Set data baru
        pegawaiDetails.setNip(request.getNip());
        Branch branch = branchService.findBranchById(request.getBranchId());
        pegawaiDetails.setBranch(branch);
        pegawaiDetails.setStatusPegawai(StatusPegawai.valueOf(request.getStatusPegawai()));

        // 🔥 Jika user adalah SUPER_ADMIN, dia bisa mengubah role pegawai
        if (userService.hasRole(user.getId(), "SUPER_ADMIN") && request.getRole() != null) {
            Role newRole = roleService.getRoleByName(request.getRole().getName());
            if (newRole == null) {
                throw new CustomException("Role tidak valid", HttpStatus.BAD_REQUEST);
            }
            targetUser.setRole(newRole);
        }

        // Simpan perubahan ke database
        savePegawaiDetails(pegawaiDetails);
        userService.saveUser(targetUser); // Simpan role jika diubah

        return "Pegawai details updated successfully!";
    }
}
