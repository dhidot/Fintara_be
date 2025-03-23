package com.sakuBCA.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
@AllArgsConstructor
public class DashboardResponseDTO {
    private String role;
    private List<String> menu;
    private String message;
}
