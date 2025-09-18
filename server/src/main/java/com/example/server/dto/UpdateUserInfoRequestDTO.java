package com.example.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserInfoRequestDTO {
    private String fullName;
    private String phone;
}
