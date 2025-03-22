package com.sakuBCA.dtos;

import com.sakuBCA.enums.StatusPegawai;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PegawaiDetailsDTO {
    private Integer id;
    private String nip;
    private Integer branchId;
    private StatusPegawai statusPegawai;

    public PegawaiDetailsDTO(Integer id, String nip, Integer branchId, StatusPegawai statusPegawai) {
        this.id = id;
        this.nip = nip;
        this.branchId = branchId;
        this.statusPegawai = statusPegawai;
    }
}
