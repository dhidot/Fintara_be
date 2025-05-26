package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.models.User;
import com.fintara.repositories.DashboardRepository;
import com.fintara.repositories.LoanApprovalRepository;
import com.fintara.repositories.LoanRequestRepository;
import com.fintara.repositories.UserRepository;
import com.fintara.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    @Autowired
    private DashboardRepository dashboardRepository;
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
    @Autowired
    private LoanRequestRepository loanRequestRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private LoanApprovalService loanApprovalService;

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

    public Map<String, Integer> getLoanRequestCountsForDashboard() {
        User currentUser = userService.getAuthenticatedUser();
        String role = currentUser.getRole().getName(); // Misal: ROLE_MARKETING, ROLE_BRANCH_MANAGER, ROLE_BACK_OFFICE

        int toCheckCount = 0;
        int checkedByUserCount = loanApprovalService.countDistinctLoanRequestsByHandledBy(currentUser.getId());

        switch (role) {
            case "MARKETING":
                toCheckCount = loanRequestRepository.countByStatusNameAndMarketingId("REVIEW", currentUser.getId());
                break;
            case "BRANCH_MANAGER":
                UUID branchId = currentUser.getPegawaiDetails().getBranch().getId();
                toCheckCount = loanRequestRepository.countByStatusNameAndBranchId("DIREKOMENDASIKAN_MARKETING", branchId);
                break;
            case "BACK_OFFICE":
                UUID branchIdBo = currentUser.getPegawaiDetails().getBranch().getId();
                toCheckCount = loanRequestRepository.countByStatusNameAndBranchId("DISETUJUI_BM", branchIdBo);
                break;
            default:
                return (Map<String, Integer>) emptyResponse();
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("loanRequestsToCheck", toCheckCount);
        result.put("loanRequestsCheckedByUser", checkedByUserCount);

        return result;
    }

    private ResponseEntity<ApiResponse<Map<String, Integer>>> emptyResponse() {
        return ResponseEntity.ok(ApiResponse.success("No data for this role", Collections.emptyMap()));
    }
}
