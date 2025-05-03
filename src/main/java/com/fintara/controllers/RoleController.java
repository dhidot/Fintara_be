package com.fintara.controllers;

import com.fintara.dtos.superAdminDTO.RoleDTO;
import com.fintara.dtos.superAdminDTO.RoleUpdateRequest;
import com.fintara.dtos.superAdminDTO.RoleWithFeatureCount;
import com.fintara.models.Role;
import com.fintara.responses.ApiResponse;
import com.fintara.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        try {
            List<RoleDTO> roleDTOs = roleService.getAllRoles();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", roleDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve roles", null));
        }
    }

    @Secured("FEATURE_GET_ROLE_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable UUID id) {
        try {
            Role role = roleService.getRoleById(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve role", null));
        }
    }

    @Secured("FEATURE_GET_ALL_ROLE")
    @GetMapping("/with-feature-count")
    public ResponseEntity<ApiResponse<List<RoleWithFeatureCount>>> getRolesWithFeatureCount() {
        List<RoleWithFeatureCount> roles = roleService.getAllRolesWithFeatureCount();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", roles));
    }

    @Secured("FEATURE_ADD_ROLE")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        Role createdRole = roleService.addRole(role).getBody();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Role created successfully", createdRole));
    }

    @Secured("FEATURE_UPDATE_ROLE")
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> editRole(@PathVariable UUID id, @RequestBody RoleUpdateRequest request) {
        roleService.editRole(id, request);
        Map<String, String> message = Map.of("message", "Role berhasil diubah!");
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Role updated successfully", message));
    }

    @Secured("FEATURE_DELETE_ROLE")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteRole(@PathVariable UUID id) {
        Map<String, String> message = roleService.deleteRole(id).getBody();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Role deleted successfully", message));
    }
}
