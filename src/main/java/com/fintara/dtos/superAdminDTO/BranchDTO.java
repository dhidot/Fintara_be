package com.fintara.dtos.superAdminDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchDTO {
    private String name;
    private String address;
    private Double latitude;  // Menambahkan latitude
    private Double longitude;
}

