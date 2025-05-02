package com.sakuBCA.controllers;

import com.sakuBCA.models.LoanStatus;
import com.sakuBCA.services.LoanStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public class LoanStatusController {
    @Autowired
    LoanStatusService loanStatusService;

    // ✅ Create Loan Status
    @Secured("FEATURE_ADD_LOAN_STATUS")
    @PostMapping
    public ResponseEntity<LoanStatus> createLoanStatus(@RequestBody LoanStatus loanStatus) {
        LoanStatus createdLoanStatus = loanStatusService.createLoanStatus(loanStatus);
        return ResponseEntity.ok(createdLoanStatus);
    }

    // ✅ Get All Loan Statuses
    @Secured("FEATURE_GET_ALL_LOAN_STATUS")
    @GetMapping
    public ResponseEntity<List<LoanStatus>> getAllLoanStatuses() {
        List<LoanStatus> loanStatuses = loanStatusService.getAllLoanStatuses();
        return ResponseEntity.ok(loanStatuses);
    }

    // ✅ Get Loan Status by ID
    @Secured("FEATURE_GET_LOAN_STATUS_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<LoanStatus> getLoanStatusById(@PathVariable UUID id) {
        LoanStatus loanStatus = loanStatusService.getLoanStatusById(id);
        return ResponseEntity.ok(loanStatus);
    }

    // ✅ Update Loan Status
    @Secured("FEATURE_UPDATE_LOAN_STATUS")
    @PutMapping("/{id}")
    public ResponseEntity<LoanStatus> updateLoanStatus(@PathVariable UUID id, @RequestBody LoanStatus loanStatus) {
        LoanStatus updatedLoanStatus = loanStatusService.updateLoanStatus(id, loanStatus);
        return ResponseEntity.ok(updatedLoanStatus);
    }

    // ✅ Delete Loan Status
    @Secured("FEATURE_DELETE_LOAN_STATUS")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoanStatus(@PathVariable UUID id) {
        loanStatusService.deleteLoanStatus(id);
        return ResponseEntity.noContent().build();
    }
}
