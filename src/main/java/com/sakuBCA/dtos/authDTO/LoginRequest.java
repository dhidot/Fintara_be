package com.sakuBCA.dtos.authDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class LoginRequest {
    private String email;
    private String password;
}
