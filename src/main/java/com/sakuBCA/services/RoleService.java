package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.Role;
import com.sakuBCA.repositories.RoleRepository;
import com.sakuBCA.dtos.superAdminDTO.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new CustomException("Role " + roleName + " tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role tidak ditemukan!", HttpStatus.NOT_FOUND));
    }

    public List<RoleDTO> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAllWithFeatures();
            logger.info("Jumlah Role: {}", roles.size());

            return roles.stream()
                    .map(role -> new RoleDTO(role))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching roles: ", e);
            throw new CustomException("Error fetching roles", HttpStatus.BAD_REQUEST);
        }
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
