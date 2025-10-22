package com.example.server.dto.request;

import com.example.server.validator.FieldsNotEqual;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@FieldsNotEqual(field = "currentPassword", fieldMatch = "newPassword", message = "CURRENT_PASSWORD_CANNOT_MATCH_NEW_PASSWORD")
public class PasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
}
