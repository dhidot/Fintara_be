package com.fintara.dtos.superAdminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RoleWithFeatureCount{
    private UUID id;
    private String name;
    private int featureCount;
}

