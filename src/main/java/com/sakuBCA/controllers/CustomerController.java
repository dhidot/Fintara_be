package com.sakuBCA.controllers;


import com.sakuBCA.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.sakuBCA.services.CustomerService;
import com.sakuBCA.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @Autowired
    private UserService userService;

    @Secured("FEATURE_GET_ALL_CUSTOMER")
    @GetMapping("/all")
    public ResponseEntity<List<UserWithCustomerResponseDTO>> getAllCustomer(){
        List<UserWithCustomerResponseDTO> customer = customerService.getAllCustomer();
        return ResponseEntity.ok(customerService.getAllCustomer());
    }

    @Secured("FEATURE_GET_CUSTOMER_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserWithCustomerResponseDTO> getCustomerUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getCustomerUserById(id));
    }
}
