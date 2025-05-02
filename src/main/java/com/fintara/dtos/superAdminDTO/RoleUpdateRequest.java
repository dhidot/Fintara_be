package com.fintara.dtos.superAdminDTO;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RoleUpdateRequest {
    private String name;
    private List<UUID> featureIds;

    // getter dan setter
}
