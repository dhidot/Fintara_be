package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.dtos.superAdminDTO.BranchDTO;
import com.sakuBCA.models.Branch;
import com.sakuBCA.repositories.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private static final Logger logger = LoggerFactory.getLogger(BranchService.class);

    private final BranchRepository branchRepository;

    // 🔹 CREATE Branch
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        if (branchRepository.existsByName(branch.getName())) {
            throw new CustomException("Branch sudah ada!", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(branchRepository.save(branch));
    }

    // READ Semua Branch
    public Map<UUID, String> getAllBranches() {
        try {
            List<Branch> branches = branchRepository.findAll();
            if (branches.isEmpty()) {
                logger.warn("Branch list is empty.");
            } else {
                logger.info("Fetched {} branches", branches.size());
            }
            return branches.stream()
                    .collect(Collectors.toMap(Branch::getId, Branch::getName));
        } catch (Exception e) {
            logger.error("Error fetching branches: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    // 🔹 READ Branch by ID
    public BranchDTO getBranchById(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID " + id + " tidak ditemukan", HttpStatus.NOT_FOUND));

        // Konversi model ke DTO
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setName(branch.getName());
        branchDTO.setAddress(branch.getAddress());

        return branchDTO;
    }

    //find branch by id
    public Branch findBranchById(UUID id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    // 🔹 UPDATE Branch
    private BranchDTO mapToDTO(Branch branch) {
        BranchDTO dto = new BranchDTO();
        dto.setName(branch.getName());
        dto.setAddress(branch.getAddress());
        return dto;
    }

    public BranchDTO updateBranch(UUID id, BranchDTO request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));

        // Cek apakah nama baru sudah digunakan oleh branch lain
        if (branchRepository.findByName(request.getName()).isPresent()) {
            throw new CustomException("Nama branch ini sudah digunakan", HttpStatus.BAD_REQUEST);
        }

        // **Pastikan field yang tidak diperbarui tetap dipertahankan**
        if (request.getName() != null) {
            branch.setName(request.getName());
        }
        if (request.getAddress() != null) {
            branch.setAddress(request.getAddress());
        }

        branchRepository.save(branch);

        return mapToDTO(branch);
    }


    // 🔹 DELETE Branch
    public void deleteBranch(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));

        branchRepository.delete(branch);
    }

    // Nearest Branch to customer
    public UUID findNearestBranch(double latitude, double longitude) {
        return branchRepository.findNearestBranch(latitude, longitude);
    }

    // Find By ID
    public Branch findById(UUID id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));
    }
}
