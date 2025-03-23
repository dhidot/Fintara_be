package com.sakuBCA.services;

import com.sakuBCA.dtos.exceptions.CustomException;
import com.sakuBCA.models.Role;
import com.sakuBCA.repositories.RoleRepository;
import org.springframework.http.HttpStatus;
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
            throw new CustomException("Role sudah ada!", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(roleRepository.save(role));
    }

    public ResponseEntity<String> editRole(Integer id, Role role) {
        Role roleData = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));

        roleData.setName(role.getName());
        roleRepository.save(roleData);

        return ResponseEntity.ok("Role berhasil diubah!");
    }

    public ResponseEntity<String> deleteRole(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));

        roleRepository.delete(role);

        return ResponseEntity.ok("Role berhasil dihapus!");
    }
}
