package com.sakuBCA.controllers;

import com.sakuBCA.dtos.loanRequestDTO.LoanRequestApprovalDTO;
import com.sakuBCA.dtos.loanRequestDTO.LoanRequestDTO;
import com.sakuBCA.dtos.superAdminDTO.LoanReviewDTO;
import com.sakuBCA.models.LoanRequest;
import com.sakuBCA.models.User;
import com.sakuBCA.services.LoanRequestService;
import com.sakuBCA.services.LoanStatusService;
import com.sakuBCA.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loan-requests")
@RequiredArgsConstructor
public class LoanRequestController {
    @Autowired
    private LoanRequestService loanRequestService;
    @Autowired
    private LoanStatusService loanStatusService;
    @Autowired
    private UserService userService;

    @Secured("FEATURE_CREATE_LOAN_REQUEST")
    @PostMapping
    public ResponseEntity<LoanRequest> createLoanRequest(@RequestBody LoanRequestDTO request) {
        // Mendapatkan informasi customer yang sedang login dan memanggil service untuk membuat loan request
        LoanRequest loanRequest = loanRequestService.createLoanRequest(
                request.getAmount(),
                request.getTenor(),
                request.getLatitude(),
                request.getLongitude()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(loanRequest);
    }

    /********** APPROVAL MARKETING **********/
    @Secured("FEATURE_APPROVAL_MARKETING")
    @GetMapping("/marketing/all")
    public ResponseEntity<List<LoanRequestApprovalDTO>> getLoanRequestsForMarketing() {
        User currentMarketing = userService.getAuthenticatedUser();

        List<LoanRequestApprovalDTO> loanRequests = loanRequestService.getLoanRequestsByMarketing(currentMarketing.getId());

        return ResponseEntity.ok(loanRequests);
    }

    @Secured("FEATURE_APPROVAL_MARKETING")
    @PutMapping("/review/{loanRequestId}")
    public ResponseEntity<String> reviewLoanRequest(
            @PathVariable UUID loanRequestId,
            @RequestBody LoanReviewDTO loanReviewDTO) {

        System.out.println("DEBUG RAW REQUEST: " + loanReviewDTO);

        User currentMarketing = userService.getAuthenticatedUser();
        loanRequestService.reviewLoanRequest(loanRequestId, currentMarketing.getId(), loanReviewDTO.getIsApproved(), loanReviewDTO.getNotes());

        return ResponseEntity.ok("Review berhasil diproses");
    }


    /********** APPROVAL BM **********/
    // Get All Loan Requests for BM on Branch that is not yet approved
    @Secured("FEATURE_APPROVAL_BM")
    @GetMapping("/branch-manager/all")
    public ResponseEntity<List<LoanRequestApprovalDTO>> getLoanRequestsForBranchManager() {
        User currentBranchManager = userService.getAuthenticatedUser();
        List<LoanRequestApprovalDTO> loanRequests = loanRequestService.getLoanRequestsForBranchManager(currentBranchManager.getId());

        return ResponseEntity.ok(loanRequests);
    }

    @Secured("FEATURE_APPROVAL_BM")
    @PutMapping("/branch-manager/review/{loanRequestId}")
    public ResponseEntity<String> reviewLoanRequestByBM(
            @PathVariable UUID loanRequestId,
            @RequestBody LoanReviewDTO loanReviewDTO) {

        User currentBM = userService.getAuthenticatedUser();
        loanRequestService.reviewLoanRequestByBM(loanRequestId, currentBM.getId(), loanReviewDTO.getIsApproved(), loanReviewDTO.getNotes());

        return ResponseEntity.ok("Review oleh Branch Manager berhasil diproses");
    }


    /********** DISBURSE BACK OFFICE **********/
    @Secured("FEATURE_DISBURSE")
    @GetMapping("/back-office/all")
    public ResponseEntity<List<LoanRequestApprovalDTO>> getLoanRequestsForBackOffice() {
        User currentBO = userService.getAuthenticatedUser();
        List<LoanRequestApprovalDTO> loanRequests = loanRequestService.getLoanRequestsForBackOffice(currentBO.getId());

        return ResponseEntity.ok(loanRequests);
    }

    @Secured("FEATURE_DISBURSE")
    @PutMapping("/back-office/disburse/{loanRequestId}")
    public ResponseEntity<String> disburseLoanRequest(@PathVariable UUID loanRequestId) {
        User currentBO = userService.getAuthenticatedUser();
        loanRequestService.disburseLoanRequest(loanRequestId, currentBO.getId());

        return ResponseEntity.ok("Loan request berhasil dicairkan");
    }
}
