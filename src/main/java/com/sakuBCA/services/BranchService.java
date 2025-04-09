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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private static final Logger logger = LoggerFactory.getLogger(BranchService.class);

    private final BranchRepository branchRepository;

    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        if (branchRepository.existsByName(branch.getName())) {
            throw new CustomException("Branch sudah ada!", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(branchRepository.save(branch));
    }

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

    public BranchDTO getBranchById(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID " + id + " tidak ditemukan", HttpStatus.NOT_FOUND));

        // Konversi model ke DTO
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setName(branch.getName());
        branchDTO.setAddress(branch.getAddress());

        return branchDTO;
    }

    public Branch findBranchById(UUID id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    public Branch findBranchByName(String branchName) {
        return branchRepository.findByName(branchName)
                .orElseThrow(() -> new CustomException("Branch tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    public UUID findNearestBranch(double latitude, double longitude) {
        List<Branch> allBranches = branchRepository.findAll();

        return allBranches.stream()
                .min(Comparator.comparing(branch -> haversineDistance(
                        latitude, longitude,
                        branch.getLatitude(), branch.getLongitude())))
                .map(Branch::getId)
                .orElse(null);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius bumi dalam KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

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

    public void deleteBranch(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));

        branchRepository.delete(branch);
    }

}
