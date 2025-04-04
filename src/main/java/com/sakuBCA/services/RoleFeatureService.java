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

    public List<String> getFeaturesByRole(UUID roleId) {
        Role role = roleService.getRoleById(roleId);

        List<RoleFeature> roleFeatures = roleFeatureRepository.findByRole(role);
        return roleFeatures.stream().map(rf -> rf.getFeature().getName()).collect(Collectors.toList());
    }
}
