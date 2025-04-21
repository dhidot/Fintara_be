package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.RoleDTO;
import com.sakuBCA.dtos.superAdminDTO.RoleUpdateRequest;
import com.sakuBCA.dtos.superAdminDTO.RoleWithFeatureCount;
import com.sakuBCA.models.Role;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.services.RoleFeatureService;
import com.sakuBCA.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;


    @Secured("FEATURE_ROLE_ACCESS")
    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        try {
            List<RoleDTO> roleDTOs = roleService.getAllRoles(); // Langsung ambil DTO dari service
            return ResponseEntity.ok(roleDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Secured("FEATURE_ROLE_ACCESS")
    @GetMapping("/get/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID id) {
        try {
            Role role = roleService.getRoleById(id);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Secured("FEATURE_ROLE_ACCESS")
    @GetMapping("/with-feature-count")
    public ResponseEntity<List<RoleWithFeatureCount>>getRolesWithFeatureCount() {
        return ResponseEntity.ok(roleService.getAllRolesWithFeatureCount());
    }

    @Secured("FEATURE_ROLE_ACCESS")
    @PostMapping("/add")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        System.out.println("Menerima request POST: " + role.getName());
        return roleService.addRole(role);
    }

    @PutMapping("/edit/{id}")
    @Secured("FEATURE_ROLE_ACCESS")
    public ResponseEntity<?> editRole(@PathVariable UUID id, @RequestBody RoleUpdateRequest request) {
        roleService.editRole(id, request);
        return ResponseEntity.ok(Map.of("message", "Role berhasil diubah!"));
    }

    @Secured("FEATURE_ROLE_ACCESS")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable UUID id) {
        System.out.println("Menerima request DELETE: " + id);
        return roleService.deleteRole(id);
    }

}
