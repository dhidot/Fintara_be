package com.fintara.controllers;

import com.fintara.dtos.pegawaiDTO.PegawaiDetailsRequestDTO;
import com.fintara.responses.ApiResponse;
import com.fintara.services.PegawaiDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/pegawaiprofile")
public class PegawaiDetailsController {
    @Autowired
    private PegawaiDetailsService pegawaiDetailsService;

    @Secured("FEATURE_UPDATE_EMPLOYEE_PROFILE")
    @PutMapping("/update/{idPegawai}")
    public ResponseEntity<ApiResponse<String>> updatePegawaiDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable("idPegawai") UUID idPegawai,
            @RequestBody PegawaiDetailsRequestDTO request) {

        String message = pegawaiDetailsService.updatePegawaiDetails(token, idPegawai, request);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Employee details updated successfully", message));
    }
}
