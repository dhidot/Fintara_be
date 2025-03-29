package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.RegisterPegawaiRequest;
import com.sakuBCA.dtos.superAdminDTO.UpdatePegawaiRequest;
import com.sakuBCA.dtos.superAdminDTO.UserWithPegawaiResponse;
import com.sakuBCA.models.User;
import com.sakuBCA.services.PegawaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Secured("FEATURE_EMPLOYEE_ACCESS")
    @PostMapping("/register")
    public ResponseEntity<User> registerPegawai(@RequestHeader("Authorization") String token, @Valid @RequestBody RegisterPegawaiRequest request) {
        Logger logger = LoggerFactory.getLogger(PegawaiController.class);

        // Log token yang diterima
        logger.info("Received token: {}", token);
        User pegawai = pegawaiService.registerPegawai(
                request.getName(),
                request.getEmail(),
                request.getRole(),
                request.getNip(),
                request.getBranchId(),
                request.getStatusPegawai()
        );

        return ResponseEntity.ok(pegawai);
    }

    @Secured("FEATURE_EMPLOYEE_ACCESS")
    @GetMapping
    public ResponseEntity<List<UserWithPegawaiResponse>> getAllPegawai() {
        List<UserWithPegawaiResponse> pegawai = pegawaiService.getAllPegawai();
        return ResponseEntity.ok(pegawaiService.getAllPegawai());
    }

    @Secured("FEATURE_EMPLOYEE_ACCESS")
    @GetMapping("/{id}")
    public ResponseEntity<UserWithPegawaiResponse> getPegawaiById(@PathVariable UUID id) {
        UserWithPegawaiResponse pegawai = pegawaiService.getPegawaiById(id);
        return ResponseEntity.ok(pegawai);
    }

    @Secured("FEATURE_EMPLOYEE_ACCESS")
    @PutMapping("/{id}")
    public ResponseEntity<UserWithPegawaiResponse> updatePegawai(
            @PathVariable UUID id,
            @RequestBody UpdatePegawaiRequest request,
            @RequestHeader("Authorization") String token) {

        UserWithPegawaiResponse updatedPegawai = pegawaiService.updatePegawai(id, request);
        return ResponseEntity.ok(updatedPegawai);
    }

    @Secured("FEATURE_EMPLOYEE_ACCESS")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePegawai(@PathVariable UUID id) {
        pegawaiService.deletePegawai(id);
        return ResponseEntity.ok("Pegawai dengan ID " + id + " berhasil dihapus");
    }

}
