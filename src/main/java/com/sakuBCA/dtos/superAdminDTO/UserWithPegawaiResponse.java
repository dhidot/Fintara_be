package com.sakuBCA.dtos.superAdminDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithPegawaiResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private PegawaiDetailsDTO pegawaiDetails;
}
