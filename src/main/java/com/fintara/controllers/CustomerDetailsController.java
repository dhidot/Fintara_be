package com.fintara.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintara.dtos.customerDTO.CustomerProfileUpdateDTO;
import com.fintara.responses.ApiResponse;
import com.fintara.services.CustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("v1/profilecustomer")
public class CustomerDetailsController {

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Secured("FEATURE_UPDATE_CUSTOMER_PROFILE")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomerDetails(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token,
            @RequestParam("ktpPhoto") MultipartFile ktpPhoto,  // Menerima file foto KTP
            @RequestParam("request") String requestJson) {    // Menerima data lainnya dalam bentuk JSON

        // Deserialize requestJson ke DTO
        CustomerProfileUpdateDTO request = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            request = objectMapper.readValue(requestJson, CustomerProfileUpdateDTO.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request data")
            );
        }

        String result = customerDetailsService.updateCustomerDetails(id, token, request, ktpPhoto);
        return ResponseEntity.ok(
                ApiResponse.success("Customer profile updated successfully", result)
        );
    }

    @Secured("FEATURE_GET_CUSTOMER_PROFILE")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCustomerDetails(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {

        Object customerProfile = customerDetailsService.getCustomerProfile(token, id);
        return ResponseEntity.ok(ApiResponse.success("Customer profile retrieved successfully", customerProfile));
    }
}
