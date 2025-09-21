package com.example.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserInfoRequestDTO {
    @NotBlank(message = "Full name cannot be empty")
    private String fullName;
    @Pattern(
            regexp = "^0[0-9]{9,10}$",
            message = "Phone number must start with 0 and be 10–11 digits"
    )
    private String phone;
}
