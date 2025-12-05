package com.example.server.service;

import com.example.server.dto.response.GlobalStatsResponse;
import com.example.server.entity.Donation;
import com.example.server.entity.Project;
import com.example.server.repository.DonationsRepository;
import com.example.server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.server.dto.request.MailBodyRequest;
import com.example.server.repository.UserRepository;

@Service
@Transactional
public class DonationService {

    @Autowired
    private DonationsRepository donationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Tạo donation mới
    public Donation createDonation(Donation donation) {
        System.out.println("[CREATE_DONATION] Tạo do Donation của test ");

        donation.setDonatedAt(LocalDateTime.now());
        donation.setPaymentStatus(Donation.PaymentStatus.failed);
        //Donation savedDonation = donationRepository.save(donation);

        // Cập nhật raised amount của project ngay sau khi tạo donation thành công
//        updateProjectRaisedAmount(savedDonation.getProjectId());
//        System.out.println("[CREATE_DONATION] Đã cập nhật raisedAmount cho project " + savedDonation.getProjectId());

        // BEGIN: Gửi email cảm ơn cho user
//        Optional<com.example.server.entity.User> userOpt = userRepository.findById(savedDonation.getDonorId());
//        if (userOpt.isPresent()) {
//            String toEmail = userOpt.get().getEmail();
//            String name = userOpt.get().getFullName();
//            String subject = "Cảm ơn bạn đã quyên góp cho dự án!";
//            String body = "Xin chào " + name + ",\n\nCảm ơn bạn đã quyên góp số tiền " + savedDonation.getAmount() + " cho dự án (ID: " + savedDonation.getProjectId() + "). Chúng tôi trân trọng sự đóng góp của bạn.\n\nTrân trọng,\nBan quản trị";
//            emailService.sendSimpleMessage(
//              MailBodyRequest.builder()
//                .to(toEmail)
//                .subject(subject)
//                .text(body)
//                .build()
//            );
//        }
        // END: Gửi email cảm ơn

        return donationRepository.save(donation);
    }

    // Cập nhật payment status
    public Donation updatePaymentStatus(Long donationId, Donation.PaymentStatus status) {
        System.out.println("[PAYMENT_STATUS] REQUEST update donationId=" + donationId + " => status: " + status);
        Optional<Donation> donationOpt = donationRepository.findById(donationId);
        if (donationOpt.isPresent()) {
            Donation donation = donationOpt.get();
            System.out.println("[PAYMENT_STATUS] BEFORE: donationId=" + donation.getDonationId() + ", status: " + donation.getPaymentStatus());
            donation.setPaymentStatus(status);
            Donation result = donationRepository.save(donation);
            System.out.println("[PAYMENT_STATUS] AFTER: donationId=" + result.getDonationId() + ", status: " + result.getPaymentStatus());

            // Nếu payment thành công, cập nhật raised amount của project
            if (status == Donation.PaymentStatus.success) {
                updateProjectRaisedAmount(donation.getProjectId());
                System.out.println("[PAYMENT_STATUS] Đã cập nhật raisedAmount cho project " + donation.getProjectId());

                userRepository.findById(donation.getDonorId()).ifPresent(user -> {
                    String toEmail = user.getEmail();
                    String name = user.getFullName();
String subject = "Cảm ơn bạn đã quyên góp cho dự án!";
                    String body = "Xin chào " + name + ",\n\n" +
                            "Cảm ơn bạn đã quyên góp số tiền " + donation.getAmount() +
                            " cho dự án (ID: " + donation.getProjectId() + "). " +
                            "Chúng tôi trân trọng sự đóng góp của bạn.\n\n" +
                            "Trân trọng,\nBan quản trị";
                    emailService.sendSimpleMessage(
                            MailBodyRequest.builder()
                                    .to(toEmail)
                                    .subject(subject)
                                    .text(body)
                                    .build()
                    );
                });
            }

            return result;
        } else {
            System.out.println("[PAYMENT_STATUS] NOT FOUND donationId=" + donationId);
        }
        return null;
    }

    // Cập nhật raised amount của project
    protected void updateProjectRaisedAmount(Long projectId) {
        BigDecimal totalRaised = donationRepository.calculateTotalDonationsByProject(projectId);
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setRaisedAmount(totalRaised);
            projectRepository.save(project);
        }
    }

    // Lấy tất cả donations (không phân trang)
    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    // Lấy tất cả donations với phân trang (mọi trạng thái)
    public Page<Donation> getAllDonations(Pageable pageable) {
        return donationRepository.findAll(pageable);
    }

    // Lấy donations theo project
    public List<Donation> getDonationsByProject(Long projectId) {
        return donationRepository.findByProjectId(projectId);
    }

    // Lấy donations theo project với pagination
    public Page<Donation> getDonationsByProject(Long projectId, Pageable pageable) {
        return donationRepository.findByProjectId(projectId, pageable);
    }

    // Lấy donations theo donor
    public List<Donation> getDonationsByDonor(Long donorId) {
        return donationRepository.findByDonorId(donorId);
    }

    // Lấy donations theo donor với pagination
    public Page<Donation> getDonationsByDonor(Long donorId, Pageable pageable) {
        return donationRepository.findByDonorId(donorId, pageable);
    }

    // Lấy donation theo ID
    public Optional<Donation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    // Tính tổng donations của một project
    public BigDecimal getTotalDonationsByProject(Long projectId) {
        return donationRepository.calculateTotalDonationsByProject(projectId);
    }

    // Tính tổng donations của một donor
    public BigDecimal getTotalDonationsByDonor(Long donorId) {
        return donationRepository.calculateTotalDonationsByDonor(donorId);
    }

    public GlobalStatsResponse getGlobalStats() {
        BigDecimal totalRaised = donationRepository.calculateTotalDonations();
        Long totalDonors = donationRepository.countTotalDonors();
        return new GlobalStatsResponse(
                totalDonors != null ? totalDonors : 0L,
                totalRaised != null ? totalRaised : BigDecimal.ZERO
        );
    }

    // Đếm số donor của một project
    public Long countDonorsByProject(Long projectId) {
        return donationRepository.countUniqueDonorsByProject(projectId);
    }

    // Đếm số project của một donor
    public Long countProjectsByDonor(Long donorId) {
        return donationRepository.countUniqueProjectsByDonor(donorId);
    }

    // Lấy donations gần nhất (chỉ các giao dịch thành công)
    public Page<Donation> getRecentDonations(Pageable pageable) {
        return donationRepository.findRecentSuccessfulDonations(pageable);
    }

    // Xóa donation
    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }

    // Cập nhật orderId cho donation
    public void updateDonationOrderId(Long donationId, String orderId) {
        Optional<Donation> donationOpt = donationRepository.findById(donationId);
        if (donationOpt.isPresent()) {
            Donation donation = donationOpt.get();
            donation.setOrderId(orderId);
            donationRepository.save(donation);
        }
    }

    // Lấy donation theo orderId
    public Donation getDonationByOrderId(String orderId) {
        return donationRepository.findByOrderId(orderId);
    }

    public Page<Donation> searchDonations(String paymentMethod, String paymentStatus, LocalDateTime from, LocalDateTime to, BigDecimal amountFrom, BigDecimal amountTo, String sortBy, Pageable pageable) {
        // Create pageable with sorting
        Pageable sortedPageable = pageable;
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "newest":
                    sortedPageable = PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by("donatedAt").descending()
                    );
                    break;
                case "highest":
                    sortedPageable = PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by("amount").descending()
                    );
                    break;
                default:
                    sortedPageable = PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by("donatedAt").descending()
                    );
                    break;
            }
        }

        // Convert string to enum
        Donation.PaymentMethod paymentMethodEnum = null;
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            try {
                paymentMethodEnum = Donation.PaymentMethod.valueOf(paymentMethod.toLowerCase());
            } catch (IllegalArgumentException e) {
                // Invalid category, will be ignored
            }
        }

        Donation.PaymentStatus paymentStatusEnum = null;
        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            try {
                paymentStatusEnum = Donation.PaymentStatus.valueOf(paymentStatus.toLowerCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, will be ignored
            }
        }

        return donationRepository.findDonationsWithFilters(paymentMethodEnum, paymentStatusEnum, from, to, amountFrom, amountTo, sortBy, sortedPageable);
    }
}