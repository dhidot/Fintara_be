package com.sakuBCA.services;

import com.sakuBCA.repositories.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private final DashboardRepository dashboardRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();

        try {
            dashboardData.put("totalUsers", dashboardRepository.getTotalUsers());
            dashboardData.put("totalRoles", dashboardRepository.getTotalRoles());
        } catch (Exception e) {
            logger.error("Gagal mengambil data dashboard: {}", e.getMessage());
            dashboardData.put("totalUsers", 0);
            dashboardData.put("totalRoles", 0);
        }

        return dashboardData;
    }
}
