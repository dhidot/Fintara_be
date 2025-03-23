package com.sakuBCA.dtos;

import lombok.Getter;
import lombok.Setter;
import com.sakuBCA.enums.StatusPegawai;

@Getter
@Setter
public class RegisterPegawaiRequest {
    private String name;
    private String email;
    private String role;
    private String nip;
    private Long branchId;
    private StatusPegawai statusPegawai; // Menggunakan Enum
}
