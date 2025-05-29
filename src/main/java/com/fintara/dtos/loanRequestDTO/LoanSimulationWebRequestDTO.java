package com.fintara.dtos.loanRequestDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanSimulationWebRequestDTO {
    private BigDecimal amount;
    private Integer tenor;
    private String plafondName;
}
