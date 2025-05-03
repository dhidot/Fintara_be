package com.fintara.controllers;

import com.fintara.dtos.superAdminDTO.RoleFeatureRequest;
import com.fintara.models.Feature;
import com.fintara.responses.ApiResponse;
import com.fintara.services.RoleFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("v1/role-features")
public class RoleFeatureController {

    @Autowired
    private RoleFeatureService roleFeatureService;

    @Secured("FEATURE_ASSIGN_ROLE_FEATURE")
    @PostMapping("/assign-multiple-features")
    public ResponseEntity<ApiResponse<Map<String, String>>> assignMultipleFeaturesToRole(@RequestBody RoleFeatureRequest request) {
        roleFeatureService.assignMultipleFeaturesToRole(request.getRoleId(), request.getFeatureIds());

        Map<String, String> message = Map.of("message", "Semua fitur berhasil ditambahkan ke role.");
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Features assigned successfully", message));
    }

    @Secured("FEATURE_GET_FEATURES_BY_ROLE_ID")
    @GetMapping("/{roleId}/features")
    public ResponseEntity<ApiResponse<List<Feature>>> getFeaturesByRoleId(@PathVariable UUID roleId) {
        List<Feature> features = roleFeatureService.getFeaturesByRole(roleId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Features retrieved successfully", features));
    }
}
