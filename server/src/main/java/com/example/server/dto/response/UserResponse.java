package com.example.server.dto.response;

import com.example.server.entity.AuthenticationProvider;
import com.example.server.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    Long userId;
    String fullName;
    String email;
    String phone;
    User.Status status;
    LocalDateTime createdAt;
    String authProvider;
    Set<RoleResponse> roles;
}
