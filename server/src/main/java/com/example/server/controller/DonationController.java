package com.example.server.controller;

import com.example.server.dto.response.DonationResponse;
import com.example.server.entity.PaymentStatus;
import com.example.server.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/donations")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping
    public List<DonationResponse> getAllDonations() {
        return donationService.getAllDonations();
    }

    @PutMapping("/{id}/status")
    public DonationResponse updateDonationStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        PaymentStatus newStatus = PaymentStatus.valueOf(status.toLowerCase());
        return donationService.updateDonationStatus(id, newStatus);
    }
}
