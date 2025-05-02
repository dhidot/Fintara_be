package com.sakuBCA.controllers;

import com.sakuBCA.config.security.UserDetailsImpl;
import com.sakuBCA.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.sakuBCA.dtos.superAdminDTO.UserResponseDTO;
import com.sakuBCA.models.User;
import com.sakuBCA.services.CloudinaryService;
import com.sakuBCA.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured("FEATURE_GET_ALL_USER")
    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update/foto")
    public ResponseEntity<Map<String, String>> updateFotoProfil(
            @RequestParam("foto") MultipartFile foto,
            Authentication authentication
    ) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String fotoUrl = cloudinaryService.uploadImage(foto.getBytes());
        user.setFotoUrl(fotoUrl);
        userService.saveUser(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Foto profil berhasil diperbarui.");
        response.put("fotoUrl", fotoUrl);
        return ResponseEntity.ok(response);
    }
}
