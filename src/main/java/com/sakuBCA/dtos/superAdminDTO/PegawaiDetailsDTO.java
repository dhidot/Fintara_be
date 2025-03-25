package com.sakuBCA.dtos.superAdminDTO;

import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.models.PegawaiDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PegawaiDetailsDTO {
    private UUID id;
    private String nip;
    private UUID branchId;
    private StatusPegawai statusPegawai;

    public PegawaiDetailsDTO(PegawaiDetails pegawaiDetails) {
        this.id = pegawaiDetails.getId();
        this.nip = pegawaiDetails.getNip();
        this.branchId = pegawaiDetails.getBranch().getId();
        this.statusPegawai = pegawaiDetails.getStatusPegawai();
    }
}
