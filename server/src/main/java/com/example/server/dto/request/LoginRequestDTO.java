package com.example.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "EMAIL_EMPTY") @Email(message = "INVALID_EMAIL")
    private String email;
    @NotBlank(message = "PASSWORD_EMPTY")
    private String password;
}
