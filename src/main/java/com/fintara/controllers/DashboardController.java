package com.fintara.controllers;

import com.fintara.models.User;
import com.fintara.responses.ApiResponse;
import com.fintara.services.DashboardService;
import com.fintara.services.LoanRequestService;
import com.fintara.services.UserService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    UserService userService;

    @Secured("FEATURE_DASHBOARD")
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboard() {
        Map<String, Long> summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully", summary));
    }

    //@Secured("FEATURE_DASHBOARD")
    @GetMapping("/loan")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getLoanRequestCountsForDashboard() {
        Map<String, Integer> counts = dashboardService.getLoanRequestCountsForDashboard();
        return ResponseEntity.ok(ApiResponse.success("Successfully fetch loan request count",counts));
    }
}
