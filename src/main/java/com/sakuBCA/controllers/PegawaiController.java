package com.sakuBCA.controllers;

import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.services.PegawaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pegawai")
@RequiredArgsConstructor
public class PegawaiController {
    private final PegawaiService pegawaiService;

    @PostMapping
    public ResponseEntity<PegawaiDetails> savePegawai(@RequestBody PegawaiDetails pegawai) {
        return ResponseEntity.ok(pegawaiService.savePegawaiDetails(pegawai));
    }
}
