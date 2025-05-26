package com.fintara.dtos.pegawaiDTO;

import com.fintara.enums.JenisKelamin;
import com.fintara.enums.StatusPegawai;
import com.fintara.models.PegawaiDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PegawaiDetailsDTO {
    private UUID id;
    private String nip;
    private String branchName;
    private StatusPegawai statusPegawai;
    private JenisKelamin jenisKelamin;

    public PegawaiDetailsDTO(PegawaiDetails pegawaiDetails) {
        this.id = pegawaiDetails.getId();
        this.nip = pegawaiDetails.getNip();
        this.branchName = pegawaiDetails.getBranch().getName();
        this.statusPegawai = pegawaiDetails.getStatusPegawai();
        this.jenisKelamin = pegawaiDetails.getJenisKelamin();
    }
}
