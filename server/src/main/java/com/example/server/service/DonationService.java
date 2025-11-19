package com.example.server.service;

import com.example.server.entity.Donation;
import com.example.server.entity.Project;
import com.example.server.repository.DonationsRepository;
import com.example.server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.server.service.NotificationProducer;
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

    // Tạo donation mới
    public Donation createDonation(Donation donation) {
        donation.setDonatedAt(LocalDateTime.now());
        donation.setPaymentStatus(Donation.PaymentStatus.pending);
        return donationRepository.save(donation);
    }

    // Cập nhật payment status
    public Donation updatePaymentStatus(Long donationId, Donation.PaymentStatus status) {
        Optional<Donation> donationOpt = donationRepository.findById(donationId);
        if (donationOpt.isPresent()) {
            Donation donation = donationOpt.get();
            donation.setPaymentStatus(status);

            // Nếu payment thành công, cập nhật raised amount của project
            if (status == Donation.PaymentStatus.success) {
                updateProjectRaisedAmount(donation.getProjectId());

                // ---- Notify donation thành công qua queue ----
                // Lấy email donor
                if (donation.getDonorId() != null) {
                    userRepository.findById(donation.getDonorId()).ifPresent(user -> {
                        String subject = "Cảm ơn bạn đã ủng hộ dự án!";
                        String text = "Bạn đã ủng hộ thành công cho dự án (ID: " + donation.getProjectId() + ") số tiền: " + donation.getAmount() + ". Cảm ơn bạn rất nhiều!";
                        com.example.server.dto.request.MailBodyRequest mailBody = com.example.server.dto.request.MailBodyRequest.builder()
                            .to(user.getEmail())
                            .subject(subject)
                            .text(text)
                            .build();
                        notificationProducer.sendNotify(mailBody);
                    });
                }
            }

            return donationRepository.save(donation);
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
}
