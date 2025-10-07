package com.example.server.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// UserUpdate admin using for updating roles
public class UserUpdateRequest {
    String fullName;
    @Pattern(
            regexp = "^0[0-9]{9,10}$",
            message = "PHONE_INVALID"
    )
    String phone;
    List<String> roles;
}
