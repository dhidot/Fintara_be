package com.sakuBCA.controllers;

import com.sakuBCA.dtos.CustomerDetailsRequest;
import com.sakuBCA.dtos.PegawaiDetailsRequest;
import com.sakuBCA.enums.StatusPegawai;
import com.sakuBCA.enums.UserType;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.repositories.CustomerDetailsRepository;
import com.sakuBCA.repositories.PegawaiDetailsRepository;
import com.sakuBCA.repositories.UserRepository;
import com.sakuBCA.services.ProfileService;
import com.sakuBCA.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
