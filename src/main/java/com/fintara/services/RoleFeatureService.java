package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.models.Role;
import com.fintara.models.Feature;
import com.fintara.models.RoleFeature;
import com.fintara.repositories.RoleFeatureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleFeatureService {

    @Autowired
    private RoleFeatureRepository roleFeatureRepository;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    private FeatureService featureService;

    public void assignFeatureToRole(UUID roleId, UUID featureId) {
        Role role = roleService.getRoleById(roleId);
        Feature feature = featureService.getFeatureById(featureId);

        RoleFeature roleFeature = RoleFeature.builder()
                .role(role)
                .feature(feature)
                .build();

        roleFeatureRepository.save(roleFeature);
    }

    public void assignMultipleFeaturesToRole(UUID roleId, List<UUID> featureIds) {
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            throw new CustomException("Role tidak ditemukan", HttpStatus.BAD_REQUEST);
        }

        for (UUID featureId : featureIds) {
            Feature feature = featureService.getFeatureById(featureId);
            if (feature == null) {
                throw new CustomException("Fitur dengan ID " + featureId + " tidak ditemukan", HttpStatus.BAD_REQUEST);
            }

            boolean alreadyAssigned = roleFeatureRepository.existsByRoleAndFeature(role, feature);
            if (!alreadyAssigned) {
                RoleFeature roleFeature = RoleFeature.builder()
                        .role(role)
                        .feature(feature)
                        .build();

                roleFeatureRepository.save(roleFeature);
            }
        }
    }

    public List<Feature> getFeaturesByRole(UUID roleId) {
        List<RoleFeature> roleFeatures = roleFeatureRepository.findByRoleId(roleId);
        return roleFeatures.stream()
                .map(roleFeature -> roleFeature.getFeature()) // Ambil fitur dari roleFeature
                .collect(Collectors.toList());
    }

    public List<Feature> getFeaturesByRoleId(UUID roleId) {
        // Mengambil semua fitur yang terkait dengan role berdasarkan roleId
        return roleFeatureRepository.findByRoleId(roleId)
                .stream()
                .map(roleFeature -> roleFeature.getFeature())
                .collect(Collectors.toList());
    }

    // Metode untuk menghapus role-feature berdasarkan roleId
    @Transactional
    public void deleteByRoleId(UUID roleId) {
        roleFeatureRepository.deleteByRoleId(roleId);
    }
}
