package com.example.server.dto;

import com.example.server.entity.Donation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationDTO {

    private Long donationId;
    private Long projectId;
    private Long donorId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime donatedAt;

    // Thông tin bổ sung từ project (optional)
    private String projectTitle;
    private String donorName;

    public static DonationDTO fromEntity(Donation donation) {
        DonationDTO dto = new DonationDTO();
        dto.setDonationId(donation.getDonationId());
        dto.setProjectId(donation.getProjectId());
        dto.setDonorId(donation.getDonorId());
        dto.setAmount(donation.getAmount());
        dto.setPaymentMethod(donation.getPaymentMethod().name().toLowerCase());
        dto.setPaymentStatus(donation.getPaymentStatus().name().toLowerCase());
        dto.setDonatedAt(donation.getDonatedAt());
        return dto;
    }

    public Donation toEntity() {
        Donation donation = new Donation();
        donation.setProjectId(this.projectId);
        donation.setDonorId(this.donorId);
        donation.setAmount(this.amount);

        if (this.paymentMethod != null) {
            donation.setPaymentMethod(Donation.PaymentMethod.valueOf(this.paymentMethod.toLowerCase()));
        }

        if (this.paymentStatus != null) {
            donation.setPaymentStatus(Donation.PaymentStatus.valueOf(this.paymentStatus.toLowerCase()));
        }

        return donation;
    }
}
