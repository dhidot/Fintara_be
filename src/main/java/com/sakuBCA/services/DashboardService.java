package com.sakuBCA.services;

import com.sakuBCA.repositories.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private final DashboardRepository dashboardRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PegawaiService pegawaiService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private PlafondService plafondService;

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

    public Map<String, Long> getDashboardSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalPegawai", pegawaiService.count());
        summary.put("totalCustomers", customerService.count());
        summary.put("totalRoles", roleService.count());
        summary.put("totalBranches", branchService.count());
        summary.put("totalPlafonds", plafondService.count());
        return summary;
    }
}
