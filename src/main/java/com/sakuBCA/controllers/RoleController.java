package com.sakuBCA.controllers;

import com.sakuBCA.models.Role;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final RoleService roleService;

    public RoleController(RoleRepository roleRepository, RoleService roleService) {
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    // Mendapatkan semua role
    @GetMapping("/all")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PreAuthorize("hasAuthority('Super Admin')")
    @PostMapping("/create")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        System.out.println("Menerima request POST: " + role.getName());
        return roleService.createRole(role);
    }

    @PreAuthorize("hasAuthority('Super Admin')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editRole(@PathVariable Integer id, @RequestBody Role role) {
        System.out.println("Menerima request PUT: " + role.getName());
        return roleService.editRole(id, role);
    }

    @PreAuthorize("hasAuthority('Super Admin')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Integer id) {
        System.out.println("Menerima request DELETE: " + id);
        return roleService.deleteRole(id);
    }

}
