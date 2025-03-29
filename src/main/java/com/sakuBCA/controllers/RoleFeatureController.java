package com.sakuBCA.controllers;

import com.sakuBCA.services.RoleFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/role-features")
public class RoleFeatureController {

    @Autowired
    private RoleFeatureService roleFeatureService;

    @Secured("FEATURE_ROLE_FEATURE_ACCESS")
    @PostMapping("/assign")
    public String assignFeatureToRole(@RequestParam UUID roleId, @RequestParam UUID featureId) {
        roleFeatureService.assignFeatureToRole(roleId, featureId);
        return "Feature assigned to role successfully!";
    }

    @Secured("FEATURE_ROLE_FEATURE_ACCESS")
    @PostMapping("/{roleId}/{featureId}")
    public ResponseEntity<String> assignFeature(@PathVariable UUID roleId, @PathVariable UUID featureId) {
        roleFeatureService.assignFeatureToRole(roleId, featureId);
        return ResponseEntity.ok("Feature berhasil ditambahkan ke role");
    }
}
