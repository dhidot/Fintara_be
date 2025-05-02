package com.fintara.controllers;

import com.fintara.dtos.superAdminDTO.RoleDTO;
import com.fintara.dtos.superAdminDTO.RoleUpdateRequest;
import com.fintara.dtos.superAdminDTO.RoleWithFeatureCount;
import com.fintara.models.Role;
import com.fintara.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;


    @Secured("FEATURE_GET_ALL_ROLE")
    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        try {
            List<RoleDTO> roleDTOs = roleService.getAllRoles(); // Langsung ambil DTO dari service
            return ResponseEntity.ok(roleDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Secured("FEATURE_GET_ROLE_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID id) {
        try {
            Role role = roleService.getRoleById(id);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Secured("FEATURE_GET_ALL_ROLE")
    @GetMapping("/with-feature-count")
    public ResponseEntity<List<RoleWithFeatureCount>>getRolesWithFeatureCount() {
        return ResponseEntity.ok(roleService.getAllRolesWithFeatureCount());
    }

    @Secured("FEATURE_ADD_ROLE")
    @PostMapping("/add")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    @Secured("FEATURE_UPDATE_ROLE")
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editRole(@PathVariable UUID id, @RequestBody RoleUpdateRequest request) {
        roleService.editRole(id, request);
        return ResponseEntity.ok(Map.of("message", "Role berhasil diubah!"));
    }

    @Secured("FEATURE_DELETE_ROLE")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> deleteRole(@PathVariable UUID id) {
        return roleService.deleteRole(id);
    }

}
