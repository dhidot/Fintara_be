package com.sakuBCA.dtos.superAdminDTO;

import lombok.*;

@Getter
@Setter
public class PegawaiDetailsRequest {
    private String nip;
    private Integer branchId;
    private String statusPegawai;
}
