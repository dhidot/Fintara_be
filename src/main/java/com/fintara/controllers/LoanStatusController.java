package com.fintara.controllers;

import com.fintara.models.LoanStatus;
import com.fintara.responses.ApiResponse;
import com.fintara.services.LoanStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/loan-statuses")
public class LoanStatusController {

    @Autowired
    private LoanStatusService loanStatusService;

    // ✅ Create Loan Status
    @Secured("FEATURE_ADD_LOAN_STATUS")
    @PostMapping
    public ResponseEntity<ApiResponse<LoanStatus>> createLoanStatus(@RequestBody LoanStatus loanStatus) {
        LoanStatus createdLoanStatus = loanStatusService.createLoanStatus(loanStatus);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Loan status successfully created", createdLoanStatus));
    }

    // ✅ Get All Loan Statuses
    @Secured("FEATURE_GET_ALL_LOAN_STATUS")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanStatus>>> getAllLoanStatuses() {
        List<LoanStatus> loanStatuses = loanStatusService.getAllLoanStatuses();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All loan statuses fetched successfully", loanStatuses));
    }

    // ✅ Get Loan Status by ID
    @Secured("FEATURE_GET_LOAN_STATUS_BY_ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanStatus>> getLoanStatusById(@PathVariable UUID id) {
        LoanStatus loanStatus = loanStatusService.getLoanStatusById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan status fetched successfully", loanStatus));
    }

    // ✅ Update Loan Status
    @Secured("FEATURE_UPDATE_LOAN_STATUS")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanStatus>> updateLoanStatus(@PathVariable UUID id, @RequestBody LoanStatus loanStatus) {
        LoanStatus updatedLoanStatus = loanStatusService.updateLoanStatus(id, loanStatus);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan status successfully updated", updatedLoanStatus));
    }

    // ✅ Delete Loan Status
    @Secured("FEATURE_DELETE_LOAN_STATUS")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLoanStatus(@PathVariable UUID id) {
        loanStatusService.deleteLoanStatus(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Loan status successfully deleted", null));
    }
}
