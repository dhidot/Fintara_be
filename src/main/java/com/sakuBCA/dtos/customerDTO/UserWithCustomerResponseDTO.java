package com.sakuBCA.dtos.customerDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserWithCustomerResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private CustomerDetailsDTO customerDetails;
}
