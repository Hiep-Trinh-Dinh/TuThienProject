package com.example.server.service;

import com.example.server.dto.response.DonationResponse;
import com.example.server.entity.Donation;
import com.example.server.entity.PaymentStatus;
import com.example.server.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    public List<DonationResponse> getAllDonations() {
        List<Donation> donations = donationRepository.findAll();
        return donations.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public DonationResponse updateDonationStatus(Long donationId, PaymentStatus newStatus) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));
        donation.setPaymentStatus(newStatus);
        donationRepository.save(donation);
        return toResponse(donation);
    }

    public DonationResponse toResponse(Donation donation) {
        DonationResponse resp = new DonationResponse();
        resp.setDonationId(donation.getDonationId());
        resp.setDonorId(donation.getDonor() != null ? donation.getDonor().getUserId() : null);
        resp.setDonorName(donation.getDonor() != null ? donation.getDonor().getFullName() : null);
        resp.setProjectId(donation.getProject() != null ? donation.getProject().getProjectId() : null);
        resp.setProjectName(donation.getProject() != null ? donation.getProject().getTitle() : null);
        resp.setAmount(donation.getAmount());
        resp.setPaymentMethod(donation.getPaymentMethod() != null ? donation.getPaymentMethod().name() : null);
        resp.setPaymentStatus(donation.getPaymentStatus() != null ? donation.getPaymentStatus().name() : null);
        resp.setDonatedAt(donation.getDonatedAt());
        return resp;
    }
}
