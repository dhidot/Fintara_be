package com.sakuBCA.dtos.superAdminDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BranchDetailsResponse {
    private UUID id;
    private String name;
}
