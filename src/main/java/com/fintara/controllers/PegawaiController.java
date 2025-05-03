package com.fintara.controllers;

import com.fintara.dtos.pegawaiDTO.RegisterPegawaiRequestDTO;
import com.fintara.dtos.pegawaiDTO.RegisterPegawaiResponseDTO;
import com.fintara.dtos.superAdminDTO.UserWithPegawaiResponseDTO;
import com.fintara.responses.ApiResponse;
import com.fintara.services.PegawaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<RegisterPegawaiResponseDTO>> registerPegawai(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RegisterPegawaiRequestDTO request) {
        RegisterPegawaiResponseDTO response = pegawaiService.registerPegawai(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Employee successfully registered", response));
    }

    @Secured("FEATURE_GET_ALL_EMPLOYEE")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserWithPegawaiResponseDTO>>> getAllPegawai() {
        List<UserWithPegawaiResponseDTO> pegawai = pegawaiService.getAllPegawai();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All employees fetched successfully", pegawai));
    }

    @Secured("FEATURE_PROFILE_EMPLOYEE")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserWithPegawaiResponseDTO>> getMyProfile() {
        UserWithPegawaiResponseDTO myProfile = pegawaiService.getMyProfile();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Employee profile fetched successfully", myProfile));
    }

    @Secured("FEATURE_GET_EMPLOYEE_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserWithPegawaiResponseDTO>> getPegawaiById(@PathVariable UUID id) {
        UserWithPegawaiResponseDTO pegawai = pegawaiService.getPegawaiById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Employee fetched successfully", pegawai));
    }

    @Secured("FEATURE_DELETE_EMPLOYEE")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePegawai(@PathVariable UUID id) {
        pegawaiService.deletePegawai(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Employee successfully deleted", "Employee with ID " + id + " has been deleted"));
    }
}
