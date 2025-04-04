package com.sakuBCA.controllers;


import com.sakuBCA.dtos.superAdminDTO.UserWithCustomerResponseDTO;
import com.sakuBCA.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @Secured("FEATURE_CUSTOMER_ACCESS")
    @GetMapping("/all")
    public ResponseEntity<List<UserWithCustomerResponseDTO>> getAllCustomer(){
        List<UserWithCustomerResponseDTO> customer = customerService.getAllCustomer();
        return ResponseEntity.ok(customerService.getAllCustomer());
    }
}
