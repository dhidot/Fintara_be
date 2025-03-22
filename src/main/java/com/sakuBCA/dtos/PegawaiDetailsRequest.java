package com.sakuBCA.dtos;

import lombok.*;

@Getter
@Setter
public class PegawaiDetailsRequest {
    private String nip;
    private Integer branchId;
    private String statusPegawai;
}
