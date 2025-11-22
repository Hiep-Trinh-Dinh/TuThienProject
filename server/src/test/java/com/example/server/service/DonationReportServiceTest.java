package com.example.server.service;

import com.example.server.entity.Donation;
import com.example.server.repository.DonationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DonationReportServiceTest {
    @Mock private DonationsRepository donationRepository;
    @InjectMocks private DonationReportService donationReportService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void exportDonationsToExcel_ReturnsByteArray() throws Exception {
        Donation donation = new Donation();
        donation.setDonationId(1L);
        donation.setDonorId(2L);
        donation.setProjectId(3L);
        donation.setAmount(BigDecimal.valueOf(1000));
        donation.setPaymentMethod(Donation.PaymentMethod.momo);
        donation.setPaymentStatus(Donation.PaymentStatus.success);
        donation.setDonatedAt(LocalDateTime.now());
        when(donationRepository.findAll()).thenReturn(Collections.singletonList(donation));
        byte[] result = donationReportService.exportDonationsToExcel();
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void exportDonationsToExcel_EmptyList_ReturnsByteArray() throws Exception {
        when(donationRepository.findAll()).thenReturn(Collections.emptyList());
        byte[] result = donationReportService.exportDonationsToExcel();
        assertThat(result).isNotNull();
    }

    @Test
    void exportDonationsToExcel_MultipleDonations_ReturnsByteArray() throws Exception {
        Donation d1 = new Donation();
        d1.setDonationId(1L);
        d1.setAmount(BigDecimal.valueOf(1000));
        d1.setPaymentMethod(Donation.PaymentMethod.momo);
        d1.setPaymentStatus(Donation.PaymentStatus.success);
        Donation d2 = new Donation();
        d2.setDonationId(2L);
        d2.setAmount(BigDecimal.valueOf(2000));
        d2.setPaymentMethod(Donation.PaymentMethod.momo);
        d2.setPaymentStatus(Donation.PaymentStatus.success);
        when(donationRepository.findAll()).thenReturn(Arrays.asList(d1, d2));
        byte[] result = donationReportService.exportDonationsToExcel();
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void exportDonationsToExcel_NullPaymentMethod_HandlesGracefully() throws Exception {
        Donation donation = new Donation();
        donation.setDonationId(1L);
        donation.setAmount(BigDecimal.valueOf(1000));
        donation.setPaymentMethod(null);
        donation.setPaymentStatus(Donation.PaymentStatus.success);
        when(donationRepository.findAll()).thenReturn(Collections.singletonList(donation));
        byte[] result = donationReportService.exportDonationsToExcel();
        assertThat(result).isNotNull();
    }
}

