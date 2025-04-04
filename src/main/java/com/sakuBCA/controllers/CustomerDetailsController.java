package com.sakuBCA.controllers;

import com.sakuBCA.dtos.customerDTO.CustomerProfileUpdateDTO;
import com.sakuBCA.services.CustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profilecustomer")
public class CustomerDetailsController {
    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Secured("FEATURE_UPDATE_CUSTOMER_PROFILE")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCustomerDetails(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token,
            @RequestBody CustomerProfileUpdateDTO request) {

        String result = customerDetailsService.updateCustomerDetails(id, token, request);
        return ResponseEntity.ok(result);
    }

    @Secured("FEATURE_GET_CUSTOMER_PROFILE")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerDetails(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(customerDetailsService.getCustomerProfile(token, id));
    }

}
