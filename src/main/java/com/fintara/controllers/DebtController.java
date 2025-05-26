package com.fintara.controllers;

import com.fintara.dtos.customerDTO.DebtInfoResponseDTO;
import com.fintara.models.User;
import com.fintara.responses.ApiResponse;
import com.fintara.services.DebtService;
import com.fintara.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("v1/debt-info")
public class DebtController {

    @Autowired
    private DebtService debtInfoService;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DebtInfoResponseDTO>> getDebtInfo() {
        User currentUser = userService.getAuthenticatedUser();  // Ambil user yang sedang login
        DebtInfoResponseDTO debtInfo = debtInfoService.getDebtInfo(currentUser);  // Ambil informasi utang
        return ResponseEntity.ok(ApiResponse.success("Debt information retrieved successfully", debtInfo));
    }
}
