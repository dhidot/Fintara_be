package com.sakuBCA.dtos.superAdminDTO;

import com.sakuBCA.dtos.customerDTO.CustomerDetailsDTO;
import com.sakuBCA.dtos.pegawaiDTO.PegawaiDetailsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private CustomerDetailsDTO customerDetails;
    private PegawaiDetailsDTO pegawaiDetails;

    public UserResponseDTO(UUID id, String name, String email, String role,
                           CustomerDetailsDTO customerDetails, PegawaiDetailsDTO pegawaiDetails) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.customerDetails = customerDetails;
        this.pegawaiDetails = pegawaiDetails;
    }
}
