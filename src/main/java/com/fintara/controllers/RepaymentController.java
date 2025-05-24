package com.fintara.controllers;

import com.fintara.dtos.repaymentsDTO.RepaymentsScheduleDTO;
import com.fintara.responses.ApiResponse;
import com.fintara.services.RepaymentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/repayments")
public class RepaymentController {
    @Autowired
    private RepaymentScheduleService repaymentScheduleService;

    @GetMapping("/loan/{loanRequestId}")
    public ResponseEntity<ApiResponse<List<RepaymentsScheduleDTO>>> getByLoanRequestId(@PathVariable UUID loanRequestId) {
        List<RepaymentsScheduleDTO> data = repaymentScheduleService.getRepaymentByLoanRequestId(loanRequestId);
        return ResponseEntity.ok(ApiResponse.success("Successfully fetch Repayment Schedule",data));
    }
}
