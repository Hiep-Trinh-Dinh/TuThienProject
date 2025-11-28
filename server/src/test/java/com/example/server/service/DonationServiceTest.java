package com.example.server.service;

import com.example.server.entity.Donation;
import com.example.server.entity.User;
import com.example.server.repository.DonationsRepository;
import com.example.server.repository.ProjectRepository;
import com.example.server.repository.UserRepository;
import com.example.server.dto.request.MailBodyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DonationServiceTest {

    @InjectMocks
    private DonationService donationService;

    @Mock
    private DonationsRepository donationsRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private NotificationProducer notificationProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDonation_sendThankYouEmail() {
        Donation donation = new Donation();
        donation.setDonationId(1L);
        donation.setProjectId(2L);
        donation.setDonorId(100L);
        donation.setAmount(BigDecimal.valueOf(100000));
        donation.setPaymentStatus(Donation.PaymentStatus.failed); // trạng thái mặc định
        donation.setDonatedAt(LocalDateTime.now());

        User user = new User();
        user.setUserId(100L);
        user.setEmail("test@example.com");
        user.setFullName("Tester");

        when(donationsRepository.save(any(Donation.class))).thenReturn(donation);
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(donationsRepository.findById(1L)).thenReturn(Optional.of(donation));

        // Gọi create, KHÔNG gửi email ngay
        donationService.createDonation(donation);
        verify(emailService, never()).sendSimpleMessage(any(MailBodyRequest.class));

        // Khi update status thành success, mới gửi email
        donation.setPaymentStatus(Donation.PaymentStatus.success);
        when(donationsRepository.save(any(Donation.class))).thenReturn(donation);
        donationService.updatePaymentStatus(1L, Donation.PaymentStatus.success);
        verify(emailService, times(1)).sendSimpleMessage(any(MailBodyRequest.class));
    }

    @Test
    void createDonation_noEmailIfUserNotFound() {
        Donation donation = new Donation();
        donation.setDonorId(404L);
        donation.setPaymentStatus(Donation.PaymentStatus.pending);
        when(donationsRepository.save(any(Donation.class))).thenReturn(donation);
        when(userRepository.findById(404L)).thenReturn(Optional.empty());
        donationService.createDonation(donation);
        verify(emailService, never()).sendSimpleMessage(any());
    }

    @Test
    void updatePaymentStatus_updatesStatusAndProjectAmount() {
        Donation donation = new Donation();
        donation.setDonationId(1L);
        donation.setProjectId(2L);
        donation.setPaymentStatus(Donation.PaymentStatus.pending);

        when(donationsRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(donationsRepository.save(any(Donation.class))).thenReturn(donation);
        Donation result = donationService.updatePaymentStatus(1L, Donation.PaymentStatus.success);
        assertEquals(Donation.PaymentStatus.success, result.getPaymentStatus());
    }

    @Test
    void getAllDonations_returnList() {
        Donation d1 = new Donation();
        d1.setPaymentStatus(Donation.PaymentStatus.success);
        Donation d2 = new Donation();
        d2.setPaymentStatus(Donation.PaymentStatus.pending);
        when(donationsRepository.findAll()).thenReturn(Arrays.asList(d1, d2));
        List<Donation> list = donationService.getAllDonations();
        assertEquals(2, list.size());
    }

    @Test
    void getAllDonations_withPaging() {
        Donation d1 = new Donation();
        d1.setPaymentStatus(Donation.PaymentStatus.success);
        Page<Donation> mockPage = new PageImpl<>(Collections.singletonList(d1));
        when(donationsRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
        Page<Donation> page = donationService.getAllDonations(PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void getDonationsByProject_returnList() {
        Donation donation = new Donation();
        donation.setPaymentStatus(Donation.PaymentStatus.success);
        when(donationsRepository.findByProjectId(anyLong())).thenReturn(Collections.singletonList(donation));
        List<Donation> list = donationService.getDonationsByProject(5L);
        assertEquals(1, list.size());
    }

    @Test
    void getDonationsByDonor_returnList() {
        Donation donation = new Donation();
        donation.setPaymentStatus(Donation.PaymentStatus.success);
        when(donationsRepository.findByDonorId(anyLong())).thenReturn(Collections.singletonList(donation));
        List<Donation> list = donationService.getDonationsByDonor(11L);
        assertEquals(1, list.size());
    }

    @Test
    void deleteDonation_callsRepoDelete() {
        doNothing().when(donationsRepository).deleteById(11L);
        donationService.deleteDonation(11L);
        verify(donationsRepository, times(1)).deleteById(11L);
    }

    @Test
    void updateDonationOrderId_updatesOrderId() {
        Donation donation = new Donation();
        when(donationsRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(donationsRepository.save(any(Donation.class))).thenReturn(donation);
        donationService.updateDonationOrderId(1L, "ORDER123");
        verify(donationsRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void updateDonationOrderId_donationNotFound_doesNothing() {
        when(donationsRepository.findById(999L)).thenReturn(Optional.empty());
        donationService.updateDonationOrderId(999L, "ORDER123");
        verify(donationsRepository, never()).save(any(Donation.class));
    }

    @Test
    void getDonationsByProject_withPagination() {
        Page<Donation> mockPage = new PageImpl<>(Collections.singletonList(new Donation()));
        when(donationsRepository.findByProjectId(anyLong(), any(Pageable.class))).thenReturn(mockPage);
        Page<Donation> page = donationService.getDonationsByProject(5L, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void getDonationsByDonor_withPagination() {
        Page<Donation> mockPage = new PageImpl<>(Collections.singletonList(new Donation()));
        when(donationsRepository.findByDonorId(anyLong(), any(Pageable.class))).thenReturn(mockPage);
        Page<Donation> page = donationService.getDonationsByDonor(11L, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void getDonationById_found() {
        Donation donation = new Donation();
        when(donationsRepository.findById(1L)).thenReturn(Optional.of(donation));
        Optional<Donation> result = donationService.getDonationById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void getDonationById_notFound() {
        when(donationsRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Donation> result = donationService.getDonationById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void getTotalDonationsByProject_returnsTotal() {
        when(donationsRepository.calculateTotalDonationsByProject(anyLong())).thenReturn(BigDecimal.valueOf(50000));
        BigDecimal total = donationService.getTotalDonationsByProject(1L);
        assertEquals(BigDecimal.valueOf(50000), total);
    }

    @Test
    void getTotalDonationsByDonor_returnsTotal() {
        when(donationsRepository.calculateTotalDonationsByDonor(anyLong())).thenReturn(BigDecimal.valueOf(30000));
        BigDecimal total = donationService.getTotalDonationsByDonor(1L);
        assertEquals(BigDecimal.valueOf(30000), total);
    }

    @Test
    void countDonorsByProject_returnsCount() {
        when(donationsRepository.countUniqueDonorsByProject(anyLong())).thenReturn(10L);
        Long count = donationService.countDonorsByProject(1L);
        assertEquals(10L, count);
    }

    @Test
    void countProjectsByDonor_returnsCount() {
        when(donationsRepository.countUniqueProjectsByDonor(anyLong())).thenReturn(5L);
        Long count = donationService.countProjectsByDonor(1L);
        assertEquals(5L, count);
    }

    @Test
    void getRecentDonations_returnsPage() {
        Page<Donation> mockPage = new PageImpl<>(Collections.singletonList(new Donation()));
        when(donationsRepository.findRecentSuccessfulDonations(any(Pageable.class))).thenReturn(mockPage);
        Page<Donation> page = donationService.getRecentDonations(PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void getDonationByOrderId_returnsDonation() {
        Donation donation = new Donation();
        when(donationsRepository.findByOrderId("ORDER123")).thenReturn(donation);
        Donation result = donationService.getDonationByOrderId("ORDER123");
        assertNotNull(result);
    }

    @Test
    void updatePaymentStatus_donationNotFound_returnsNull() {
        when(donationsRepository.findById(999L)).thenReturn(Optional.empty());
        Donation result = donationService.updatePaymentStatus(999L, Donation.PaymentStatus.success);
        assertNull(result);
    }

    @Test
    void updatePaymentStatus_notSuccess_doesNotUpdateProject() {
        Donation donation = new Donation();
        donation.setDonationId(1L);
        donation.setProjectId(2L);
        donation.setPaymentStatus(Donation.PaymentStatus.pending);
        when(donationsRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(donationsRepository.save(any(Donation.class))).thenReturn(donation);
        Donation result = donationService.updatePaymentStatus(1L, Donation.PaymentStatus.failed);
        assertEquals(Donation.PaymentStatus.failed, result.getPaymentStatus());
    }
}
