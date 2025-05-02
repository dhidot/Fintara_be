package com.fintara.controllers;

import com.fintara.dtos.pegawaiDTO.RegisterPegawaiRequestDTO;
import com.fintara.dtos.pegawaiDTO.RegisterPegawaiResponseDTO;
import com.fintara.dtos.superAdminDTO.UserWithPegawaiResponseDTO;
import com.fintara.services.PegawaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/pegawai")
@RequiredArgsConstructor
public class PegawaiController {
    private final PegawaiService pegawaiService;

    @Secured("FEATURE_ADD_EMPLOYEE")
    @PostMapping("/register")
    public ResponseEntity<RegisterPegawaiResponseDTO> registerPegawai(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RegisterPegawaiRequestDTO request) {
        RegisterPegawaiResponseDTO response = pegawaiService.registerPegawai(request);
        return ResponseEntity.ok(response);
    }

    @Secured("FEATURE_GET_ALL_EMPLOYEE")
    @GetMapping
    public ResponseEntity<List<UserWithPegawaiResponseDTO>> getAllPegawai() {
        List<UserWithPegawaiResponseDTO> pegawai = pegawaiService.getAllPegawai();
        return ResponseEntity.ok(pegawaiService.getAllPegawai());
    }

    @Secured("FEATURE_PROFILE_EMPLOYEE")  // Pastikan role/feature security sudah sesuai
    @GetMapping("/me")
    public UserWithPegawaiResponseDTO getMyProfile() {
        return pegawaiService.getMyProfile();
    }

    @Secured("FEATURE_GET_EMPLOYEE_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserWithPegawaiResponseDTO> getPegawaiById(@PathVariable UUID id) {
        UserWithPegawaiResponseDTO pegawai = pegawaiService.getPegawaiById(id);
        return ResponseEntity.ok(pegawai);
    }

    @Secured("FEATURE_DELETE_EMPLOYEE")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePegawai(@PathVariable UUID id) {
        pegawaiService.deletePegawai(id);
        return ResponseEntity.ok("Pegawai dengan ID " + id + " berhasil dihapus");
    }
}
