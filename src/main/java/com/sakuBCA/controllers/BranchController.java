package com.sakuBCA.controllers;


import com.sakuBCA.dtos.superAdminDTO.BranchDTO;
import com.sakuBCA.models.Branch;
import com.sakuBCA.services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {
    @Autowired
    private BranchService branchService;

    @Secured("FEATURE_BRANCHES_ACCESS")
    @PostMapping("/add")
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        return branchService.createBranch(branch);
    }

    @Secured("FEATURE_BRANCHES_ACCESS")
    @GetMapping("/all")
    public Map<UUID, String> getAllBranches() {
        return branchService.getAllBranches();
    }

    // Get Branch By ID
    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable UUID id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable UUID id, @RequestBody BranchDTO request) {
        return ResponseEntity.ok(branchService.updateBranch(id, request));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable UUID id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok("Branch berhasil dihapus");
    }
}
