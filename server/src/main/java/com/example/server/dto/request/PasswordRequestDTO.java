package com.example.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
}
