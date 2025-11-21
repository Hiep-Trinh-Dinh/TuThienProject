package com.example.server.repository;

import com.example.server.entity.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonationsRepository extends JpaRepository<Donation, Long> {

    // Tìm donations theo project
    List<Donation> findByProjectId(Long projectId);

    // Tìm donations theo donor
    List<Donation> findByDonorId(Long donorId);

    // Tìm donations theo payment status
    List<Donation> findByPaymentStatus(Donation.PaymentStatus paymentStatus);

    // Tìm donation theo orderId
    Donation findByOrderId(String orderId);

    // Tìm donations theo project với pagination
    Page<Donation> findByProjectId(Long projectId, Pageable pageable);

    // Tìm donations theo donor với pagination
    Page<Donation> findByDonorId(Long donorId, Pageable pageable);

    // Tính tổng amount đã donate thành công cho một project
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.projectId = :projectId AND d.paymentStatus = 'success'")
    BigDecimal calculateTotalDonationsByProject(@Param("projectId") Long projectId);

    // Tính tổng amount đã donate của một user
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.donorId = :donorId AND d.paymentStatus = 'success'")
    BigDecimal calculateTotalDonationsByDonor(@Param("donorId") Long donorId);

    // Đếm số người donor unique cho một project
    @Query("SELECT COUNT(DISTINCT d.donorId) FROM Donation d WHERE d.projectId = :projectId AND d.paymentStatus = 'success'")
    Long countUniqueDonorsByProject(@Param("projectId") Long projectId);

    // Đếm số projects unique cho một donor
    @Query("SELECT COUNT(DISTINCT d.projectId) FROM Donation d WHERE d.donorId = :donorId AND d.paymentStatus = 'success'")
    Long countUniqueProjectsByDonor(@Param("donorId") Long donorId);

    // Top donations gần nhất
    @Query("SELECT d FROM Donation d WHERE d.paymentStatus = 'success' ORDER BY d.donatedAt DESC")
    Page<Donation> findRecentSuccessfulDonations(Pageable pageable);
}
