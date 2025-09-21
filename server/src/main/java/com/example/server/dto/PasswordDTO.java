package com.example.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordDTO {
    private String currentPassword;
    private String newPassword;
}
