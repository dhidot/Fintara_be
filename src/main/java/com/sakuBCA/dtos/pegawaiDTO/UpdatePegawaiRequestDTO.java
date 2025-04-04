package com.sakuBCA.dtos.pegawaiDTO;

import lombok.Data;

@Data
public class UpdatePegawaiRequestDTO {
    private String name;
    private String email;
    private String nip;
    private String branch;
    private String statusPegawai;
}