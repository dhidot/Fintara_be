package com.fintara.dtos.loanRequestDTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanPreviewResponseDTO {
    private BigDecimal requestedAmount;
    private BigDecimal disbursedAmount;
    private int tenor;
    private BigDecimal interestRate;
    private BigDecimal interestAmount;
    private BigDecimal feesAmount;
    private BigDecimal totalRepayment;
    private BigDecimal estimatedInstallment;
}
