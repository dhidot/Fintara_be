package com.sakuBCA.dtos.superAdminDTO;

import com.sakuBCA.models.Branch;
import com.sakuBCA.models.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class PegawaiDetailsRequest {
    private String nip;
    private UUID branchId;
    private String statusPegawai;
    private Role role;
}
