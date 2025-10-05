package com.example.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "Full name cannot be empty")
    private String fullName;
    @NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String passwordHash;
    @Pattern(
            regexp = "^0[0-9]{9,10}$",
            message = "Phone number must start with 0 and be 10–11 digits"
    )
    private String phone;
    private LocalDateTime createdAt;
    private List<String> roles;
}
