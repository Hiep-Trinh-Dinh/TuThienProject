package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private Long donationId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "donor_id", nullable = false)
    private Long donorId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.pending;

    @Column(name = "donated_at")
    private LocalDateTime donatedAt = LocalDateTime.now();

    @Column(name = "order_id", length = 100)
    private String orderId; // Lưu orderId từ Momo để map lại khi redirect

    // Relationship với Project entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    public enum PaymentMethod {
        vnpay, viettel_money, momo, credit_card, bank_transfer
    }

    public enum PaymentStatus {
        success, pending, failed
    }
}
