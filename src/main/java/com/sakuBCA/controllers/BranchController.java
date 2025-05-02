package com.sakuBCA.controllers;


import com.sakuBCA.dtos.superAdminDTO.BranchDTO;
import com.sakuBCA.models.Branch;
import com.sakuBCA.services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {
    @Autowired
    private BranchService branchService;

    @Secured("FEATURE_ADD_BRANCHES")
    @PostMapping("/add")
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        return branchService.createBranch(branch);
    }

    @Secured("FEATURE_GET_ALL_BRANCHES")
    @GetMapping("/all")
    public List<Branch> getAllBranches() {
        return branchService.getAllBranches();
    }

    @Secured("FEATURE_GET_BRANCHES_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable UUID id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @Secured("FEATURE_UPDATE_BRANCHES")
    @PutMapping("/update/{id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable UUID id, @RequestBody BranchDTO request) {
        return ResponseEntity.ok(branchService.updateBranch(id, request));
    }

    @Secured("FEATURE_DELETE_BRANCHES")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable UUID id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok("Branch berhasil dihapus");
    }
}
