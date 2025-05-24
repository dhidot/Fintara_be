package com.fintara.dtos.customerDTO;

import com.fintara.enums.JenisKelamin;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWithCustomerResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String fotoUrl;
    private String role;
    private CustomerDetailsDTO customerDetails;
}
