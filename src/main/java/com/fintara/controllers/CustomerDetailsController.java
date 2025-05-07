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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RestController
@RequestMapping("v1/profilecustomer")
public class CustomerDetailsController {

    @Autowired
    private CustomerDetailsService customerDetailsService;

@Secured("FEATURE_UPDATE_CUSTOMER_PROFILE")
@PutMapping("/update")
public ResponseEntity<ApiResponse<String>> updateOwnCustomerDetails(
        @RequestHeader("Authorization") String token,
        @RequestParam("ktpPhoto") MultipartFile ktpPhoto,
        @RequestParam("selfiePhoto") MultipartFile selfiePhoto,
        @RequestParam("request") String requestJson) {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDetailsController.class);

    // Log masuknya request untuk memudahkan debugging
    logger.debug("Received token: {}", token);
    logger.debug("Received request JSON: {}", requestJson);
    logger.debug("Received KTP photo: {}", ktpPhoto.getOriginalFilename());
    logger.debug("Received Selfie photo: {}", selfiePhoto.getOriginalFilename());

    CustomerProfileUpdateDTO request = null;
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        request = objectMapper.readValue(requestJson, CustomerProfileUpdateDTO.class);
    } catch (Exception e) {
        logger.error("Error parsing requestJson: {}", requestJson, e);
        return ResponseEntity.badRequest().body(
                ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request data")
        );
    }

    // Log objek yang sudah diparse
    logger.debug("Parsed DTO: {}", request);

    String result = customerDetailsService.updateOwnCustomerDetails(request, ktpPhoto, selfiePhoto);
    return ResponseEntity.ok(ApiResponse.success("Customer profile updated successfully", result));
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
