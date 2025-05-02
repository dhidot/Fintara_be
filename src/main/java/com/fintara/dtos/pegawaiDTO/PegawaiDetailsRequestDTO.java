package com.fintara.dtos.pegawaiDTO;

import com.fintara.models.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class PegawaiDetailsRequestDTO {
    private String nip;
    private UUID branchId;
    private String statusPegawai;
    private Role role;
}
