package com.fintara.dtos.customerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DebtInfoResponseDTO {
    private BigDecimal remainingPlafond;
    private Integer activeLoansCount;
    private BigDecimal totalRepayment;
}
