package com.fintara.dtos.superAdminDTO;

import com.fintara.dtos.pegawaiDTO.PegawaiDetailsDTO;
import com.fintara.enums.JenisKelamin;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UserWithPegawaiResponseDTO {
    private UUID id;
    private String name;
    private String fotoUrl;
    private String email;
    private String role;
    private PegawaiDetailsDTO pegawaiDetails;
}
