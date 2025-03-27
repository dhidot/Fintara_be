package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.BranchDetailsRequest;
import com.sakuBCA.dtos.superAdminDTO.BranchDetailsResponse;
import com.sakuBCA.models.Branch;
import com.sakuBCA.repositories.BranchRepository;
import com.sakuBCA.services.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {
    @Autowired
    private final BranchService branchService;
    @Autowired
    private final BranchRepository branchRepository;

    @Autowired
    public BranchController(BranchRepository branchRepository, BranchService branchService){
        this.branchService = branchService;
        this.branchRepository = branchRepository;
    }

    @Secured("BRANCHES_ACCESS")
    @PostMapping("/add")
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        return branchService.createBranch(branch);
    }

    @Secured("BRANCHES_ACCESS")
    @GetMapping("/all")
    public List<Branch> getAllBranches() {
        return branchService.getAllBranches();
    }

//    // 🔹 READ BY ID
//    @GetMapping("/{id}")
//    public ResponseEntity<BranchDTO> getBranchById(@PathVariable UUID id) {
//        return ResponseEntity.ok(branchService.getBranchById(id));
//    }
//
//    // 🔹 UPDATE
//    @PutMapping("/{id}")
//    public ResponseEntity<BranchDTO> updateBranch(@PathVariable UUID id, @RequestBody CreateBranchRequest request) {
//        return ResponseEntity.ok(branchService.updateBranch(id, request));
//    }
//
//    // 🔹 DELETE
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteBranch(@PathVariable UUID id) {
//        branchService.deleteBranch(id);
//        return ResponseEntity.ok("Branch berhasil dihapus");
//    }
}
