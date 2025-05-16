package com.fintara.controllers;

import com.fintara.dtos.loanRequestDTO.LoanRequestApprovalDTO;
import com.fintara.dtos.loanRequestDTO.LoanRequestDTO;
import com.fintara.dtos.loanRequestDTO.LoanRequestResponseDTO;
import com.fintara.dtos.superAdminDTO.LoanReviewDTO;
import com.fintara.models.LoanRequest;
import com.fintara.models.User;
import com.fintara.responses.ApiResponse;
import com.fintara.services.LoanRequestService;
import com.fintara.services.LoanStatusService;
import com.fintara.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/loan-requests")
@RequiredArgsConstructor
public class LoanRequestController {

    @Autowired
    private LoanRequestService loanRequestService;
    @Autowired
    private LoanStatusService loanStatusService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoanRequestResponseDTO>> createLoanRequest(@Valid @RequestBody LoanRequestDTO request) {
        LoanRequestResponseDTO loanRequestResponse = loanRequestService.createLoanRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Loan request successfully created", loanRequestResponse));
    }

    @Secured("FEATURE_REVIEW_LOAN_REQUEST")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanRequestApprovalDTO>> getLoanRequest(@PathVariable UUID id, Authentication authentication) {
        User currentUser = userService.getAuthenticatedUser();
        LoanRequest loanRequest = loanRequestService.getLoanRequestById(id);

        loanRequestService.validateAccess(currentUser, loanRequest);

        LoanRequestApprovalDTO dto = loanRequestService.mapToApprovalDTO(loanRequest);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan request details fetched successfully", dto));
    }

    /********** APPROVAL MARKETING **********/
    @Secured("FEATURE_APPROVAL_MARKETING")
    @GetMapping("/marketing/all")
    public ResponseEntity<ApiResponse<List<LoanRequestApprovalDTO>>> getLoanRequestsForMarketing() {
        User currentMarketing = userService.getAuthenticatedUser();
        List<LoanRequestApprovalDTO> loanRequests = loanRequestService.getLoanRequestsByMarketing(currentMarketing.getId());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan requests for marketing fetched successfully", loanRequests));
    }

    @Secured("FEATURE_APPROVAL_MARKETING")
    @PutMapping("/review/{loanRequestId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> reviewLoanRequest(
            @PathVariable UUID loanRequestId,
            @RequestBody LoanReviewDTO loanReviewDTO) {

        User currentMarketing = userService.getAuthenticatedUser();
        loanRequestService.reviewLoanRequest(loanRequestId, currentMarketing.getId(), loanReviewDTO.getStatus(), loanReviewDTO.getNotes());

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Marketing review successfully processed", Map.of("message", "Review Marketing berhasil diproses")));
    }

    /********** APPROVAL BM **********/
    @Secured("FEATURE_APPROVAL_BM")
    @GetMapping("/branch-manager/all")
    public ResponseEntity<ApiResponse<List<LoanRequestApprovalDTO>>> getLoanRequestsForBranchManager() {
        User currentBranchManager = userService.getAuthenticatedUser();
        List<LoanRequestApprovalDTO> loanRequests = loanRequestService.getLoanRequestsForBranchManager(currentBranchManager.getId());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan requests for Branch Manager fetched successfully", loanRequests));
    }

    @Secured("FEATURE_APPROVAL_BM")
    @PutMapping("/branch-manager/review/{loanRequestId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> reviewLoanRequestByBM(
            @PathVariable UUID loanRequestId,
            @RequestBody LoanReviewDTO loanReviewDTO) {

        User currentBM = userService.getAuthenticatedUser();
        loanRequestService.reviewLoanRequestByBM(loanRequestId, currentBM.getId(), loanReviewDTO.getStatus(), loanReviewDTO.getNotes());

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Branch Manager review successfully processed", Map.of("message", "Review BM berhasil diproses")));
    }

    /********** DISBURSE BACK OFFICE **********/
    @Secured("FEATURE_DISBURSE")
    @GetMapping("/back-office/all")
    public ResponseEntity<ApiResponse<List<LoanRequestApprovalDTO>>> getLoanRequestsForBackOffice() {
        User currentBO = userService.getAuthenticatedUser();
        List<LoanRequestApprovalDTO> loanRequests = loanRequestService.getLoanRequestsForBackOffice(currentBO.getId());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan requests for Back Office fetched successfully", loanRequests));
    }

    @Secured("FEATURE_DISBURSE")
    @PutMapping("/back-office/disburse/{loanRequestId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> disburseLoanRequest(@PathVariable UUID loanRequestId) {
        User currentBO = userService.getAuthenticatedUser();
        loanRequestService.disburseLoanRequest(loanRequestId, currentBO.getId());

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan request successfully disbursed", Map.of("message", "Loan request berhasil dicairkan")));
    }

    // get all loan requests by customer id
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<LoanRequestResponseDTO>>> getLoanRequestHistory() {
        List<LoanRequestResponseDTO> history = loanRequestService.getLoanRequestByStatuses(
                Arrays.asList("DITOLAK", "DISBURSED")
        );
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan request history", history));
    }

    @GetMapping("/in-progress")
    public ResponseEntity<ApiResponse<List<LoanRequestResponseDTO>>> getLoanRequestInProgress() {
        List<LoanRequestResponseDTO> inProgress = loanRequestService.getLoanRequestByStatuses(
                Arrays.asList("REVIEW", "DIREKOMENDASIKAN_BM") // Tambahkan status proses lain jika ada
        );
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan requests in progress", inProgress));
    }
}
