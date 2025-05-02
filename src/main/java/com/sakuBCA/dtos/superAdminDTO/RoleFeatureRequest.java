package com.sakuBCA.dtos.superAdminDTO;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RoleFeatureRequest {
    private UUID roleId;
    private List<UUID> featureIds;
}
