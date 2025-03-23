package com.sakuBCA.controllers;


import com.sakuBCA.dtos.superAdminDTO.UserWithCustomerResponse;
import com.sakuBCA.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PreAuthorize("hasAuthority('Super Admin')") // Hanya bisa diakses oleh Super Admin
    @GetMapping
    public ResponseEntity<List<UserWithCustomerResponse>> getAllCustomer(){
        List<UserWithCustomerResponse> customer = customerService.getAllCustomer();
        return ResponseEntity.ok(customerService.getAllCustomer());
    }
}
