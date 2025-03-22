package com.sakuBCA.controllers;

import com.sakuBCA.dtos.UserResponseDTO;
import com.sakuBCA.models.User;
import com.sakuBCA.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Mendapatkan semua pengguna
    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user){
        return ResponseEntity.ok(userService.registerUser(user));
    }


}
