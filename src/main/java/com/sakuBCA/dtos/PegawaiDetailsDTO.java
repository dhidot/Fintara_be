package com.sakuBCA.dtos;

import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.models.PegawaiDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PegawaiDetailsDTO {
    private Integer id;
    private String nip;
    private Integer branchId;
    private StatusPegawai statusPegawai;

    public PegawaiDetailsDTO(PegawaiDetails pegawaiDetails) {
        this.id = pegawaiDetails.getId();
        this.nip = pegawaiDetails.getNip();
        this.branchId = pegawaiDetails.getBranchId();
        this.statusPegawai = pegawaiDetails.getStatusPegawai();
    }
}
