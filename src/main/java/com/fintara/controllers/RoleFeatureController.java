package com.fintara.controllers;

import com.fintara.dtos.superAdminDTO.RoleFeatureRequest;
import com.fintara.models.Feature;
import com.fintara.services.RoleFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Map<String, String>> assignMultipleFeaturesToRole(@RequestBody RoleFeatureRequest request) {

        roleFeatureService.assignMultipleFeaturesToRole(request.getRoleId(), request.getFeatureIds());
        return ResponseEntity.ok(Map.of("message", "Semua fitur berhasil ditambahkan ke role."));
    }

    @Secured("FEATURE_GET_FEATURES_BY_ROLE_ID")
    @GetMapping("/{roleId}/features")
    public ResponseEntity<List<Feature>> getFeaturesByRoleId(@PathVariable UUID roleId) {
        List<Feature> features = roleFeatureService.getFeaturesByRole(roleId);
        return ResponseEntity.ok(features);
    }
}
