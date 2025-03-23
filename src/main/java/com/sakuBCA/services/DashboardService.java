package com.sakuBCA.services;

import com.sakuBCA.repositories.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("totalUsers", dashboardRepository.getTotalUsers());
        dashboardData.put("totalRoles", dashboardRepository.getTotalRoles());

        return dashboardData;
    }
}
