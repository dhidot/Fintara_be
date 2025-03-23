package com.sakuBCA.controllers;

import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/customer")
    public ResponseEntity<CustomerDetails> saveCustomer(@RequestBody CustomerDetails customer) {
        return ResponseEntity.ok(customerService.saveCustomerDetails(customer));
    }
}
