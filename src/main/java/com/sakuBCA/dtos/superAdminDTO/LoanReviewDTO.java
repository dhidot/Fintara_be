package com.sakuBCA.dtos.superAdminDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoanReviewDTO {
    @JsonProperty("isApproved")
    private Boolean isApproved;
    private String notes;
}