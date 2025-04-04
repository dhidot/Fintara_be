package com.sakuBCA.controllers;

import com.sakuBCA.dtos.pegawaiDTO.PegawaiDetailsRequestDTO;
import com.sakuBCA.services.PegawaiDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pegawaiprofile")
public class PegawaiDetailsController {
    @Autowired
    private PegawaiDetailsService pegawaiDetailsService;

    @Secured("FEATURE_UPDATE_PEGAWAI_PROFILE")
    @PostMapping("/update/{idPegawai}")
    public ResponseEntity<String> updatePegawaiDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable("idPegawai") UUID idPegawai,
            @RequestBody PegawaiDetailsRequestDTO request) {
        return ResponseEntity.ok(pegawaiDetailsService.updatePegawaiDetails(token, idPegawai, request));
    }
}
