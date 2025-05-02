package com.fintara.services;

import com.fintara.config.exceptions.CustomException;
import com.fintara.config.security.NameNormalizer;
import com.fintara.dtos.superAdminDTO.RoleUpdateRequest;
import com.fintara.dtos.superAdminDTO.RoleWithFeatureCount;
import com.fintara.models.Feature;
import com.fintara.models.Role;
import com.fintara.models.RoleFeature;
import com.fintara.repositories.RoleRepository;
import com.fintara.dtos.superAdminDTO.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private RoleFeatureService roleFeatureService;
    @Autowired
    private NameNormalizer nameNormalizer;

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new CustomException("Role " + roleName + " tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));
    }

    public List<RoleDTO> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAllWithFeatures();
            logger.info("Jumlah Role: {}", roles.size());

            return roles.stream()
                    .map(role -> new RoleDTO(role))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching roles: ", e);
            throw new CustomException("Error fetching roles", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        // Normalisasi nama role
        String normalizedName = nameNormalizer.normalizeRoleName(role.getName());
        role.setName(normalizedName);
        if (roleRepository.existsByName(normalizedName)) {
            throw new CustomException("Role sudah ada!", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(roleRepository.save(role));
    }

    public void editRole(UUID id, RoleUpdateRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));

        role.setName(request.getName());

        // Hapus semua relasi lama dari DB
        roleFeatureService.deleteByRoleId(role.getId());

        // Ambil fitur baru
        List<Feature> features = featureService.findAllById(request.getFeatureIds());

        // Buat relasi baru
        List<RoleFeature> newRoleFeatures = features.stream().map(feature -> {
            RoleFeature rf = new RoleFeature();
            rf.setRole(role);
            rf.setFeature(feature);
            return rf;
        }).toList();

        role.setRoleFeatures(newRoleFeatures);
        roleRepository.save(role);
    }



    public ResponseEntity<Map<String, String>> deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));

        roleRepository.delete(role);

        return ResponseEntity.ok(Map.of("message", "Role berhasil dihapus!"));
    }

    public List<RoleWithFeatureCount> getAllRolesWithFeatureCount() {
        List<Role> roles = roleRepository.findAllWithFeatures(); // Pakai fetch join kalau bisa
        return roles.stream()
                .map(role -> new RoleWithFeatureCount(
                        role.getId(),
                        role.getName(),
                        role.getRoleFeatures().size()
                ))
                .collect(Collectors.toList());
    }

    public Long count() {
        return roleRepository.count();
    }
}
