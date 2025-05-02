package com.sakuBCA.controllers;

import com.sakuBCA.dtos.loanApprovalDTO.LoanApprovalHistoryResponse;
import com.sakuBCA.dtos.loanRequestDTO.LoanApprovalDTO;
import com.sakuBCA.models.LoanApproval;
import com.sakuBCA.models.User;
import com.sakuBCA.services.LoanApprovalService;
import com.sakuBCA.services.UserService;
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
@RequestMapping("/v1/loan-approvals")
@RequiredArgsConstructor
public class LoanApprovalController {
    @Autowired
    private LoanApprovalService loanApprovalService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<LoanApproval> approveLoan(@RequestBody LoanApprovalDTO request) {
        LoanApproval loanApproval = loanApprovalService.approveLoan(
                request.getLoanRequestId(),
                request.getUserId(),
                request.getDecision()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(loanApproval);
    }

    @GetMapping("/loan-request/{loanRequestId}")
    public ResponseEntity<List<LoanApproval>> getLoanApprovals(@PathVariable UUID loanRequestId) {
        List<LoanApproval> approvals = loanApprovalService.getLoanApprovals(loanRequestId);
        return ResponseEntity.ok(approvals);
    }

    @Secured("FEATURE_APPROVAL_HISTORY")
    @GetMapping("/approval-history")
    public ResponseEntity<List<LoanApprovalHistoryResponse>> getHandledLoanRequestsByCurrentUser(Authentication authentication) {
        User currentUser = userService.getAuthenticatedUser();
        List<LoanApprovalHistoryResponse> result = loanApprovalService.getHandledApprovalsByUser(currentUser.getId());
        return ResponseEntity.ok(result);
    }
}

