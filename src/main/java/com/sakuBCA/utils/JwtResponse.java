package com.sakuBCA.utils;

import lombok.*;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> features;
    private String role;

    public JwtResponse(String token, String username, String role, List<String> features) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.features = features;
    }
}