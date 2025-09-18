package com.example.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private LocalDateTime createdAt;
    private String Role;
    private String Status;
}
