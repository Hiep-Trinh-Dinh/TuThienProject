package com.example.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// User Update của user
public class UpdateUserInfoRequestDTO {
    @NotBlank(message = "FULL_NAME_EMPTY")
    private String fullName;
    @Pattern(
            regexp = "^0[0-9]{9,10}$",
            message = "PHONE_INVALID"
    )
    private String phone;
    private String avatarUrl;
    private String coverPhotoUrl;
}
