package com.sakuBCA.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private CustomerDetailsDTO customerDetails;
    private PegawaiDetailsDTO pegawaiDetails;

    public UserResponseDTO(Integer id, String name, String email, String role,
                           CustomerDetailsDTO customerDetails, PegawaiDetailsDTO pegawaiDetails) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.customerDetails = customerDetails;
        this.pegawaiDetails = pegawaiDetails;
    }
}
