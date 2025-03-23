package com.sakuBCA.controllers;

import com.sakuBCA.dtos.superAdminDTO.UserResponseDTO;
import com.sakuBCA.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('Super Admin')") // Hanya SUPERADMIN yang boleh
    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

//    @PreAuthorize("hasRole('SUPERADMIN') or #id == authentication.principal.id") // SUPERADMIN atau user yang login sendiri
//    @GetMapping("/{id}")
//    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
//        return ResponseEntity.ok(userService.getUserById(id));
}
