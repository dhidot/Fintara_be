package com.sakuBCA.controllers;

import com.sakuBCA.dtos.customerDTO.UserWithCustomerResponseDTO;
import com.sakuBCA.dtos.superAdminDTO.UserResponseDTO;
import com.sakuBCA.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured("FEATURE_USER_ACCESS")
    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }
}
