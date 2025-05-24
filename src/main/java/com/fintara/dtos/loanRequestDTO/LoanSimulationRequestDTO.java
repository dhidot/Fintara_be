package com.fintara.dtos.loanRequestDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanSimulationRequestDTO {
    private BigDecimal amount;
    private Integer tenor;
}
