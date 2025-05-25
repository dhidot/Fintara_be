package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.models.User;
import com.fintara.utils.NameNormalizer;
import com.fintara.dtos.superAdminDTO.BranchDTO;
import com.fintara.models.Branch;
import com.fintara.repositories.BranchRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BranchService {

    private static final Logger logger = LoggerFactory.getLogger(BranchService.class);

    private final BranchRepository branchRepository;
    @Autowired
    private NameNormalizer nameNormalizer;
    @Autowired
    private UserService userService;

    public ResponseEntity<Branch> createBranch(@Valid @RequestBody Branch branch) {
        String normalizedName = nameNormalizer.normalizedName(branch.getName());
        branch.setName(normalizedName);

        if (branchRepository.existsByName(normalizedName)) {
            throw new CustomException("Branch sudah ada!", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(branchRepository.save(branch));
    }

    public List<Branch> getAllBranches() {
        try {
            List<Branch> branches = branchRepository.findAll();
            if (branches.isEmpty()) {
                logger.warn("Branch list is empty.");
            } else {
                logger.info("Fetched {} branches", branches.size());
            }
            return branches; // Kembalikan List<Branch> langsung
        } catch (Exception e) {
            logger.error("Error fetching branches: {}", e.getMessage(), e);
            return Collections.emptyList(); // Kembalikan list kosong jika terjadi error
        }
    }

    public BranchDTO getBranchById(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID " + id + " tidak ditemukan", HttpStatus.NOT_FOUND));

        // Mengambil latitude dan longitude
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setName(branch.getName());
        branchDTO.setAddress(branch.getAddress());
        branchDTO.setLatitude(branch.getLatitude());   // Pastikan ini ada
        branchDTO.setLongitude(branch.getLongitude()); // Pastikan ini ada

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

    public UUID findNearestBranchWithMarketing(double latitude, double longitude) {
        List<Branch> allBranches = branchRepository.findAll();

        // Urutkan berdasarkan jarak terdekat
        List<Branch> sortedBranches = allBranches.stream()
                .sorted(Comparator.comparing(branch -> haversineDistance(
                        latitude, longitude,
                        branch.getLatitude(), branch.getLongitude())))
                .toList();

        // Cari cabang yang ada marketing
        for (Branch branch : sortedBranches) {
            List<User> marketingList = userService.getMarketingByBranch(branch.getId());
            if (!marketingList.isEmpty()) {
                return branch.getId();
            }
        }

        return null; // Tidak ada cabang dengan marketing
    }

//    public UUID findNearestBranchWithMarketing(double latitude, double longitude) {
//        List<Branch> allBranches = branchRepository.findAll();
//
//        return allBranches.stream()
//                .sorted(Comparator.comparing(branch -> haversineDistance(
//                        latitude, longitude,
//                        branch.getLatitude(), branch.getLongitude())))
//                .filter(branch -> userService.existsByBranchAndRole_Name(branch, "MARKETING"))
//                .map(Branch::getId)
//                .findFirst()
//                .orElse(null);
//    }

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
        dto.setLatitude(branch.getLatitude());
        dto.setLongitude(branch.getLongitude());
        return dto;
    }

    public BranchDTO updateBranch(UUID id, BranchDTO request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));

        // Cek apakah nama baru sudah digunakan oleh branch lain, kecuali untuk branch itu sendiri
        if (request.getName() != null && branchRepository.findByName(request.getName()).isPresent()) {
            Branch existingBranch = branchRepository.findByName(request.getName()).get();
            if (!existingBranch.getId().equals(id)) {
                throw new CustomException("Nama branch ini sudah digunakan", HttpStatus.BAD_REQUEST);
            }
        }

        // Update properti yang diterima dari request
        if (request.getName() != null) {
            branch.setName(request.getName());
        }
        if (request.getAddress() != null) {
            branch.setAddress(request.getAddress());
        }
        if (request.getLatitude() != null) {
            branch.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            branch.setLongitude(request.getLongitude());
        }

        branchRepository.save(branch);

        return mapToDTO(branch);
    }


    public void deleteBranch(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new CustomException("Branch dengan ID ini tidak ditemukan", HttpStatus.NOT_FOUND));

        branchRepository.delete(branch);
    }

    public Long count() {
        return branchRepository.count();
    }
}
