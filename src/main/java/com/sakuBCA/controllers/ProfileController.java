package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.CustomerDetailsRequest;
import com.sakuBCA.dtos.superAdminDTO.PegawaiDetailsRequest;
import com.sakuBCA.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    //API untuk pegawai mengisi detail data diri
    @PostMapping("/customer")
    public ResponseEntity<String> updateCustomerDetails(@RequestHeader("Authorization") String token, @RequestBody CustomerDetailsRequest request){
        return ResponseEntity.ok(profileService.updateCustomerDetails(token, request));
    }

    @PostMapping("/pegawai")
    public ResponseEntity<String> updatePegawaiDetails(
            @RequestHeader("Authorization") String token,
            @RequestBody PegawaiDetailsRequest request) {
        return ResponseEntity.ok(profileService.updatePegawaiDetails(token, request));
    }
}
