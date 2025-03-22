package com.sakuBCA.services;

import com.sakuBCA.models.Role;
import com.sakuBCA.repositories.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role sudah ada!");
        }

        return ResponseEntity.ok(roleRepository.save(role));
    }
}
