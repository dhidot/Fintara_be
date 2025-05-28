package com.fintara.controllers;

import com.fintara.dtos.loanApprovalDTO.LoanApprovalHistoryResponse;
import com.fintara.dtos.loanApprovalDTO.LoanApprovalReviewerResponse;
import com.fintara.dtos.loanRequestDTO.LoanApprovalDTO;
import com.fintara.models.LoanApproval;
import com.fintara.models.User;
import com.fintara.responses.ApiResponse;
import com.fintara.services.LoanApprovalService;
import com.fintara.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/loan-approvals")
@RequiredArgsConstructor
public class LoanApprovalController {

    @Autowired
    private LoanApprovalService loanApprovalService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoanApproval>> approveLoan(@RequestBody LoanApprovalDTO request) {
        LoanApproval loanApproval = loanApprovalService.approveLoan(
                request.getLoanRequestId(),
                request.getUserId(),
                request.getDecision()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Loan successfully approved", loanApproval));
    }

    @GetMapping("/loan-request/{loanRequestId}")
    public ResponseEntity<ApiResponse<List<LoanApproval>>> getLoanApprovals(@PathVariable UUID loanRequestId) {
        List<LoanApproval> approvals = loanApprovalService.getLoanApprovals(loanRequestId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Successfully fetched loan approvals", approvals));
    }

    @Secured("FEATURE_APPROVAL_HISTORY")
    @GetMapping("/approval-history")
    public ResponseEntity<ApiResponse<List<LoanApprovalHistoryResponse>>> getHandledLoanRequestsByCurrentUser(Authentication authentication) {
        User currentUser = userService.getAuthenticatedUser();
        List<LoanApprovalHistoryResponse> result = loanApprovalService.getHandledApprovalsByUser(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Successfully fetched loan approval history", result));
    }

    @GetMapping("/{loanRequestId}")
    public ResponseEntity<ApiResponse<List<LoanApprovalReviewerResponse>>> getApprovalsByLoanRequest(@PathVariable UUID loanRequestId) {
        List<LoanApprovalReviewerResponse> approvals = loanApprovalService.getApprovalsByLoanRequestId(loanRequestId);
        return ResponseEntity.ok(ApiResponse.success("Data approvals berhasil diambil", approvals));
    }

}
