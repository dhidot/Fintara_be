package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.RegisterPegawaiRequest;
import com.sakuBCA.dtos.superAdminDTO.UpdatePegawaiRequest;
import com.sakuBCA.dtos.superAdminDTO.UserWithPegawaiResponse;
import com.sakuBCA.models.User;
import com.sakuBCA.services.PegawaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pegawai")
@RequiredArgsConstructor
public class PegawaiController {
    private final PegawaiService pegawaiService;

//    @PostMapping("/register")
//    public ResponseEntity<User> registerPegawai(
//            @RequestHeader("Authorization") String token,
//            @RequestBody RegisterPegawaiRequest request) {
//
//        User pegawai = pegawaiService.registerPegawai(
//                request.getName(),
//                request.getEmail(),
//                request.getRole(), // Tetap String
//                request.getNip(),
//                request.getBranchId(),
//                request.getStatusPegawai(),
//                token.replace("Bearer ", "")
//        );
//
//        return ResponseEntity.ok(pegawai);
//    }

    @GetMapping
    public ResponseEntity<List<UserWithPegawaiResponse>> getAllPegawai() {
        List<UserWithPegawaiResponse> pegawai = pegawaiService.getAllPegawai();
        return ResponseEntity.ok(pegawaiService.getAllPegawai());
    }

 // Hanya bisa diakses oleh Super Admin
    @GetMapping("/{id}")
    public ResponseEntity<UserWithPegawaiResponse> getPegawaiById(@PathVariable UUID id) {
        UserWithPegawaiResponse pegawai = pegawaiService.getPegawaiById(id);
        return ResponseEntity.ok(pegawai);
    }

    // Hanya bisa diakses oleh Super Admin
    @PutMapping("/{id}")
    public ResponseEntity<UserWithPegawaiResponse> updatePegawai(
            @PathVariable UUID id,
            @RequestBody UpdatePegawaiRequest request,
            @RequestHeader("Authorization") String token) {

        UserWithPegawaiResponse updatedPegawai = pegawaiService.updatePegawai(id, request);
        return ResponseEntity.ok(updatedPegawai);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePegawai(@PathVariable UUID id) {
        pegawaiService.deletePegawai(id);
        return ResponseEntity.ok("Pegawai dengan ID " + id + " berhasil dihapus");
    }

}
