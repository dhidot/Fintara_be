package com.sakuBCA.dtos;

import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.PegawaiDetails;
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
