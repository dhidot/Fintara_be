package com.fintara.security;

import lombok.*;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> features;
    private String role;
    private String name;

    public JwtResponse(String token, String username, String role, List<String> features, String name) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.features = features;
        this.name = name;
    }
}