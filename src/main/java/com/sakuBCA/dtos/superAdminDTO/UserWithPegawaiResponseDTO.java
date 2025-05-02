package com.sakuBCA.dtos.superAdminDTO;

import com.sakuBCA.dtos.pegawaiDTO.PegawaiDetailsDTO;
import com.sakuBCA.enums.JenisKelamin;
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
    private JenisKelamin jenisKelamin;
    private PegawaiDetailsDTO pegawaiDetails;
}
