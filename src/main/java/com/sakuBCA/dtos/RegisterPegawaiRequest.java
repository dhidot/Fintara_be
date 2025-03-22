package com.sakuBCA.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterPegawaiRequest {
    private String name;
    private String email;
    private String password;
    private String role;  // Role dalam bentuk string
}
