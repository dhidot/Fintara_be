package com.fintara.controllers;

import com.fintara.security.UserDetailsImpl;
import com.fintara.dtos.superAdminDTO.UserResponseDTO;
import com.fintara.models.User;
import com.fintara.responses.ApiResponse;
import com.fintara.services.CloudinaryService;
import com.fintara.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured("FEATURE_GET_ALL_USER")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Users retrieved successfully", users));
    }

    @PutMapping("/update/foto")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateFotoProfil(
            @RequestParam("foto") MultipartFile foto,
            Authentication authentication
    ) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null));
        }

        String fotoUrl = cloudinaryService.uploadImage(foto.getBytes());
        user.setFotoUrl(fotoUrl);
        userService.saveUser(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Foto profil berhasil diperbarui.");
        response.put("fotoUrl", fotoUrl);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Profile picture updated successfully", response));
    }
}
