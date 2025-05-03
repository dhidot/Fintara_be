package com.fintara.controllers;

import com.fintara.dtos.superAdminDTO.BranchDTO;
import com.fintara.models.Branch;
import com.fintara.responses.ApiResponse;
import com.fintara.services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @Secured("FEATURE_ADD_BRANCHES")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Branch>> createBranch(@RequestBody Branch branch) {
        // Create branch
        Branch result = branchService.createBranch(branch).getBody();

        // Return response with success message and the created branch as the data
        return ResponseEntity.ok(ApiResponse.success("Branch berhasil dibuat", result));
    }


    @Secured("FEATURE_GET_ALL_BRANCHES")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Branch>>> getAllBranches() {
        List<Branch> branches = branchService.getAllBranches();
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil semua branch", branches));
    }

    @Secured("FEATURE_GET_BRANCHES_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchDTO>> getBranchById(@PathVariable UUID id) {
        BranchDTO dto = branchService.getBranchById(id);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil detail branch", dto));
    }

    @Secured("FEATURE_UPDATE_BRANCHES")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<BranchDTO>> updateBranch(@PathVariable UUID id, @RequestBody BranchDTO request) {
        BranchDTO dto = branchService.updateBranch(id, request);
        return ResponseEntity.ok(ApiResponse.success("Branch berhasil diperbarui", dto));
    }

    @Secured("FEATURE_DELETE_BRANCHES")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBranch(@PathVariable UUID id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.success("Branch berhasil dihapus"));
    }
}
