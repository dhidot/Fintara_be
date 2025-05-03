package com.fintara.controllers;

import com.fintara.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.fintara.responses.ApiResponse;
import com.fintara.services.CustomerService;
import com.fintara.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;

    @Secured("FEATURE_GET_ALL_CUSTOMER")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserWithCustomerResponseDTO>>> getAllCustomer() {
        List<UserWithCustomerResponseDTO> customers = customerService.getAllCustomer();
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil semua data customer", customers));
    }

    @Secured("FEATURE_GET_CUSTOMER_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserWithCustomerResponseDTO>> getCustomerUserById(@PathVariable UUID id) {
        UserWithCustomerResponseDTO customer = userService.getCustomerUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data customer", customer));
    }
}
