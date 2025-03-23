package com.sakuBCA.dtos;

import lombok.Data;

@Data
public class UpdatePegawaiRequest {
    private String name;
    private String email;
    private String nip;
    private String branchId;
    private String statusPegawai;
}