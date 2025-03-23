package com.sakuBCA.controllers;

import com.sakuBCA.dtos.RegisterPegawaiRequest;
import com.sakuBCA.dtos.UpdatePegawaiRequest;
import com.sakuBCA.dtos.UserWithPegawaiResponse;
import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.models.User;
import com.sakuBCA.services.PegawaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pegawai")
@RequiredArgsConstructor
public class PegawaiController {
    private final PegawaiService pegawaiService;

    @PreAuthorize("hasAuthority('Super Admin')") // Hanya bisa diakses oleh Super Admin
    @PostMapping("/register")
    public ResponseEntity<User> registerPegawai(
            @RequestHeader("Authorization") String token,
            @RequestBody RegisterPegawaiRequest request) {

        User pegawai = pegawaiService.registerPegawai(
                request.getName(),
                request.getEmail(),
                request.getRole(), // Tetap String
                request.getNip(),
                request.getBranchId(),
                request.getStatusPegawai(),
                token.replace("Bearer ", "")
        );

        return ResponseEntity.ok(pegawai);
    }

    @PreAuthorize("hasAuthority('Super Admin')")
    @GetMapping
    public ResponseEntity<List<UserWithPegawaiResponse>> getAllPegawai() {
        List<UserWithPegawaiResponse> pegawai = pegawaiService.getAllPegawai();
        return ResponseEntity.ok(pegawaiService.getAllPegawai());
    }

    @PreAuthorize("hasAuthority('Super Admin')") // Hanya bisa diakses oleh Super Admin
    @GetMapping("/{id}")
    public ResponseEntity<UserWithPegawaiResponse> getPegawaiById(@PathVariable Long id) {
        UserWithPegawaiResponse pegawai = pegawaiService.getPegawaiById(id);
        return ResponseEntity.ok(pegawai);
    }

    @PreAuthorize("hasAuthority('Super Admin')") // Hanya bisa diakses oleh Super Admin
    @PutMapping("/{id}")
    public ResponseEntity<UserWithPegawaiResponse> updatePegawai(
            @PathVariable Long id,
            @RequestBody UpdatePegawaiRequest request,
            @RequestHeader("Authorization") String token) {

        System.out.println("🔹 Token diterima: " + token);
        System.out.println("🔹 Request update pegawai: " + request.toString());

        UserWithPegawaiResponse updatedPegawai = pegawaiService.updatePegawai(id, request);
        return ResponseEntity.ok(updatedPegawai);
    }


}
