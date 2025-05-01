package com.sakuBCA.controllers;

import com.sakuBCA.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @Secured("FEATURE_DASHBOARD")
    @GetMapping("/data")
    public ResponseEntity<Map<String, Long>> getDashboard() {
        Map<String, Long> summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
