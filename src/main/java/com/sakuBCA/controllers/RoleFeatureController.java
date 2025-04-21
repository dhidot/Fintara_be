package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.RoleFeatureRequest;
import com.sakuBCA.models.Feature;
import com.sakuBCA.services.RoleFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/role-features")
public class RoleFeatureController {

    @Autowired
    private RoleFeatureService roleFeatureService;

    @Secured("FEATURE_ROLE_FEATURE_ACCESS")
    @PostMapping("/assign-multiple-features")
    public ResponseEntity<Map<String, String>> assignMultipleFeaturesToRole(@RequestBody RoleFeatureRequest request) {

        roleFeatureService.assignMultipleFeaturesToRole(request.getRoleId(), request.getFeatureIds());
        return ResponseEntity.ok(Map.of("message", "Semua fitur berhasil ditambahkan ke role."));
    }

    @Secured("FEATURE_ROLE_FEATURE_ACCESS")
    @GetMapping("/{roleId}/features")
    public ResponseEntity<List<Feature>> getFeaturesByRoleId(@PathVariable UUID roleId) {
        List<Feature> features = roleFeatureService.getFeaturesByRole(roleId);
        return ResponseEntity.ok(features);
    }
}
