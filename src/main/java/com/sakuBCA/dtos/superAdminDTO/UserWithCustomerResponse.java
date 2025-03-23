package com.sakuBCA.dtos.superAdminDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithCustomerResponse {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private CustomerDetailsDTO customerDetails;
}
