package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.Branch;
import com.sakuBCA.repositories.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;

    // 🔹 CREATE Branch
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        if (branchRepository.existsByName(branch.getName())) {
            throw new CustomException("Role sudah ada!", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(branchRepository.save(branch));
    }

    // 🔹 READ Semua Branch
    @PreAuthorize("hasAuthority('Super Admin')")
    @GetMapping("/all")
    public List<Branch> getAllBranches() {
        try {
            return branchRepository.findAll();
        } catch (Exception e) {
            throw new CustomException("Terjadi kesalahan saat mengambil data cabang", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 READ Branch by ID
//    public ResponseEntity<> getBranchById(UUID id) {
//        Branch branch = branchRepository.findById(id)
//                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));
//
//        return mapToDTO(branch);
//    }

    // 🔹 UPDATE Branch
//    public BranchDTO updateBranch(UUID id, CreateBranchRequest request) {
//        Branch branch = branchRepository.findById(id)
//                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));
//
//        // Cek apakah nama baru sudah digunakan oleh branch lain
//        if (branchRepository.findByName(request.getName()).isPresent()) {
//            throw new CustomException("Nama branch ini sudah digunakan", HttpStatus.BAD_REQUEST);
//        }
//
//        branch.setName(request.getName());
//        branchRepository.save(branch);
//
//        return mapToDTO(branch);
//    }

    // 🔹 DELETE Branch
    public void deleteBranch(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));

        branchRepository.delete(branch);
    }

//    // Utility untuk mapping Entity -> DTO
//    private BranchDTO mapToDTO(Branch branch) {
//        BranchDTO dto = new BranchDTO();
//        dto.setId(branch.getId());
//        dto.setName(branch.getName());
//        return dto;
//    }
}
