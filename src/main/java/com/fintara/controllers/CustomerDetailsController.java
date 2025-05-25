package com.fintara.controllers;

import com.fintara.dtos.customerDTO.CustomerUpdateProfileRequestDTO;
import com.fintara.dtos.customerDTO.FirstTimeUpdateDTO;
import com.fintara.responses.ApiResponse;
import com.fintara.services.CustomerDetailsService;
import jakarta.validation.Valid;
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
    private static final Logger logger = LoggerFactory.getLogger(CustomerDetailsController.class);
    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Secured("FEATURE_UPDATE_CUSTOMER_PROFILE")
    @PutMapping("/first-time_update")
    public ResponseEntity<ApiResponse<String>> updateFirstLogin(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody FirstTimeUpdateDTO request
    ) {
        logger.info("Received token: {}", token);
        logger.info("Received FirstLogin Request DTO: {}", request);

        try {
            String result = customerDetailsService.firstTimeUpdateOwnCustomerDetails(request);
            return ResponseEntity.ok(ApiResponse.success("Customer profile updated successfully", result));
        } catch (Exception e) {
            logger.error("Error updating customer profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update customer profile"));
        }
    }

    @PutMapping("/update-my-profile")
    public ResponseEntity<ApiResponse<String>> updateMyProfile(@Valid @RequestBody CustomerUpdateProfileRequestDTO request) {
        String result = customerDetailsService.updateMyProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Customer Profile has been updated successfully", result));
    }

    @PostMapping("/upload-ktp")
    public ResponseEntity<ApiResponse<String>> uploadKtp(@RequestParam("file") MultipartFile file) {
        try {
            String uploadedUrl = customerDetailsService.uploadKtpPhoto(file);
            return ResponseEntity.ok(ApiResponse.success("Upload berhasil", uploadedUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Upload gagal: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-selfie-ktp")
    public ResponseEntity<ApiResponse<String>> uploadSelfie(@RequestParam("file") MultipartFile file) {
        try {
            String uploadedUrl = customerDetailsService.uploadSelfiePhoto(file);
            return ResponseEntity.ok(ApiResponse.success("Upload selfie berhasil", uploadedUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Upload gagal: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<ApiResponse<String>> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        try {
            String uploadedUrl = customerDetailsService.uploadProfilePhoto(file);
            return ResponseEntity.ok(ApiResponse.success("Upload berhasil", uploadedUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Upload gagal: " + e.getMessage()));
        }
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

//@Secured("FEATURE_UPDATE_CUSTOMER_PROFILE")
//@PutMapping("/update")
//public ResponseEntity<ApiResponse<String>> updateOwnCustomerDetails(
//        @RequestHeader("Authorization") String token,
//        @RequestParam("ktpPhoto") MultipartFile ktpPhoto,
//        @RequestParam("selfiePhoto") MultipartFile selfiePhoto,
//        @RequestParam("request") String requestJson) {
//    // Log masuknya request untuk memudahkan debugging
//    logger.info("Received token: {}", token);
//    logger.info("Received request JSON: {}", requestJson);
//    logger.info("Received KTP photo: {}", ktpPhoto.getOriginalFilename());
//    logger.info("Received Selfie photo: {}", selfiePhoto.getOriginalFilename());
//
//    CustomerProfileUpdateDTO request = null;
//    try {
//        ObjectMapper objectMapper = new ObjectMapper();
//        request = objectMapper.readValue(requestJson, CustomerProfileUpdateDTO.class);
//    } catch (Exception e) {
//        logger.error("Error parsing requestJson: {}", requestJson, e);
//        return ResponseEntity.badRequest().body(
//                ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request data")
//        );
//    }
//
//    // Log objek yang sudah diparse
//    logger.info("Parsed DTO: {}", request);
//
//    String result = customerDetailsService.updateOwnCustomerDetails(request);
//    return ResponseEntity.ok(ApiResponse.success("Customer profile updated successfully", result));
//}
