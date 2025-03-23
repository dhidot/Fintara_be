package com.sakuBCA.dtos;

import com.sakuBCA.models.PegawaiDetails;
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
