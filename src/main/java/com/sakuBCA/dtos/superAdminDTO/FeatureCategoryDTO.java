package com.sakuBCA.dtos.superAdminDTO;

import java.util.UUID;

public class FeatureCategoryDTO {
    private UUID id;
    private String name;

    public FeatureCategoryDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters dan setters
    public UUID getId() { return id; }
    public String getName() { return name; }
}
