package com.sakuBCA.services;

import com.sakuBCA.models.Role;
import com.sakuBCA.models.Feature;
import com.sakuBCA.models.RoleFeature;
import com.sakuBCA.repositories.RoleFeatureRepository;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.repositories.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleFeatureService {

    @Autowired
    private RoleFeatureRepository roleFeatureRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FeatureRepository featureRepository;

    public void assignFeatureToRole(UUID roleId, UUID featureId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new RuntimeException("Feature not found"));

        RoleFeature roleFeature = RoleFeature.builder()
                .role(role)
                .feature(feature)
                .featureName(feature.getName())
                .build();

        roleFeatureRepository.save(roleFeature);
    }

    public List<String> getFeaturesByRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role tidak ditemukan"));

        List<RoleFeature> roleFeatures = roleFeatureRepository.findByRole(role);
        return roleFeatures.stream().map(rf -> rf.getFeature().getName()).collect(Collectors.toList());
    }
}
