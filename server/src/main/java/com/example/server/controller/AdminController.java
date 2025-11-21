package com.example.server.controller;

import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.UserResponse;
import com.example.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.server.entity.Donation;
import com.example.server.service.DonationService;
import com.example.server.dto.DonationDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class AdminController {
    
    private final UserService userService;
    private final DonationService donationService;

    private Donation.PaymentStatus parseStatus(String status) {
        String normalized = status == null ? "" : status.trim();
        log.info("Parsing donation status, raw='{}', normalized='{}', available={}",
                status, normalized,
                Arrays.stream(Donation.PaymentStatus.values()).map(Enum::name).collect(Collectors.toList()));
        for (Donation.PaymentStatus s : Donation.PaymentStatus.values()) {
            if (s.name().equalsIgnoreCase(normalized)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }

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

    @PutMapping("/donations/{id}/status")
    public ResponseEntity<?> updateDonationStatusByAdmin(
            @PathVariable Long id,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        Donation.PaymentStatus paymentStatus;
        try {
            paymentStatus = parseStatus(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided for donation update: {}", status, e);
            response.put("error", "Invalid status value");
            response.put("validStatus", Arrays.stream(Donation.PaymentStatus.values()).map(Enum::name).collect(Collectors.toList()));
            response.put("received", status);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Donation updatedDonation = donationService.updatePaymentStatus(id, paymentStatus);
            if (updatedDonation != null) {
                return ResponseEntity.ok(DonationDTO.fromEntity(updatedDonation));
            } else {
                response.put("error", "Donation not found");
                response.put("donationId", id);
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            log.error("Unexpected error when updating donation status", e);
            response.put("error", "Internal server error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

