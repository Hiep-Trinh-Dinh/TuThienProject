package com.example.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "FULL_NAME_EMPTY")
    private String fullName;
    @NotBlank(message = "EMAIL_EMPTY") @Email(message = "INVALID_EMAIL")
    private String email;
    @Size(min = 6, message = "PASSWORD_INVALID")
    @NotBlank(message = "PASSWORD_EMPTY")
    private String passwordHash;
    private LocalDateTime createdAt;
}
