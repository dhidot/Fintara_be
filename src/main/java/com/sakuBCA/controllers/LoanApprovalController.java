package com.sakuBCA.controllers;

import com.sakuBCA.dtos.customerDTO.LoanApprovalDTO;
import com.sakuBCA.models.LoanApproval;
import com.sakuBCA.services.LoanApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan-approvals")
@RequiredArgsConstructor
public class LoanApprovalController {
    @Autowired
    private LoanApprovalService loanApprovalService;

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
}

