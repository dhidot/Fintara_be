package com.sakuBCA.dtos.superAdminDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoanReviewDTO {
    private String status;
    private String notes;
}