package com.example.server.dto;

import com.example.server.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AuthResponse {
    private User user;
    private String token;

    public AuthResponse(String token, User user){
        this.token = token;
        this.user = user;
    }
}
