package com.sakuBCA.controllers;

import com.sakuBCA.config.security.JwtUtils;
import com.sakuBCA.dtos.customerDTO.UpdateCustomerDetailsDTO;
import com.sakuBCA.services.BranchService;
import com.sakuBCA.services.CustomerDetailsService;
import com.sakuBCA.services.PegawaiDetailsService;
import com.sakuBCA.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profilecustomer")
public class CustomerDetailsController {
    @Autowired
    private CustomerDetailsService customerDetailsService;


    //API untuk pegawai mengisi detail data diri
    @Secured("FEATURE_CUSTOMER_PROFILE")
    @PostMapping("/update")
    public ResponseEntity<String> updateCustomerDetails(@RequestHeader("Authorization") String token, @RequestBody UpdateCustomerDetailsDTO request){
        return ResponseEntity.ok(customerDetailsService.updateCustomerDetails(token, request));
    }
}
