package com.sakuBCA.dtos.superAdminDTO;

import com.sakuBCA.dtos.pegawaiDTO.PegawaiDetailsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UserWithPegawaiResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private PegawaiDetailsDTO pegawaiDetails;
}
