package com.sakuBCA.dtos;

import com.sakuBCA.models.Role;
import com.sakuBCA.models.RoleFeature;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class RoleDTO {
    private UUID id;
    private String name;
    private List<String> features;

    public RoleDTO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.features = role.getRoleFeatures().stream()
                .map(RoleFeature::getFeatureName) // Ambil langsung dari featureName
                .collect(Collectors.toList());
    }
}