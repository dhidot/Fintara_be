package com.sakuBCA.dtos.customerDTO;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithCustomerResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private CustomerDetailsDTO customerDetails;
}
