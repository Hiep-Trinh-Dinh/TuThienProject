package com.example.server.controller;

import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.UserResponse;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class AdminController {
    
    private final UserService userService;

    /**
     * Endpoint riêng cho admin để lấy danh sách tất cả users
     * Chỉ admin mới có quyền truy cập endpoint này
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public ApiResponse<List<UserResponse>> getAllUsersForAdmin() {
        try {
            log.info("Admin requesting user list");
            List<UserResponse> users = userService.getAllUsers();
            log.info("Found {} users", users.size());
            ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setResult(users);
            return apiResponse;
        } catch (Exception e) {
            log.error("Error getting users for admin: {}", e.getMessage(), e);
            ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setCode(1001);
            apiResponse.setMessage("Error retrieving users: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            return apiResponse;
        }
    }
}

