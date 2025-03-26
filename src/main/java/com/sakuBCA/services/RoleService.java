package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.Role;
import com.sakuBCA.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        List<Role> roles = roleRepository.findAllWithFeatures();
        System.out.println("Jumlah Role: " + roles.size()); // Cek apakah ada data

        for (Role role : roles) {
            System.out.println("Role: " + role.getName() + ", Features: " + role.getRoleFeatures().size());
        }
        return roles;
    }

    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new CustomException("Role sudah ada!", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(roleRepository.save(role));
    }

    public ResponseEntity<String> editRole(UUID id, Role role) {
        Role roleData = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));

        roleData.setName(role.getName());
        roleRepository.save(roleData);

        return ResponseEntity.ok("Role berhasil diubah!");
    }

    public ResponseEntity<String> deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));

        roleRepository.delete(role);

        return ResponseEntity.ok("Role berhasil dihapus!");
    }
}
